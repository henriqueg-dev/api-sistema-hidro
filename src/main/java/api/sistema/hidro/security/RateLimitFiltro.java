package api.sistema.hidro.security;

import api.sistema.hidro.exception.ErroRespostaDTO;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Em memória: só serve para uma instância.
@Component
@RequiredArgsConstructor
public class RateLimitFiltro extends OncePerRequestFilter {

    private static final String CAMINHO_LOGIN = "/api/auth/login";

    private static final int LIMITE_ENTRADAS = 10_000;

    private final ObjectMapper objectMapper;

    @Value("${rate-limit.login.tentativas:5}")
    private int loginTentativas;

    @Value("${rate-limit.login.janela-segundos:300}")
    private long loginJanelaSegundos;

    @Value("${rate-limit.geral.requisicoes:120}")
    private int geralRequisicoes;

    @Value("${rate-limit.geral.janela-segundos:60}")
    private long geralJanelaSegundos;

    private final Map<String, Janela> janelas = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean ehLogin = CAMINHO_LOGIN.equals(request.getRequestURI());
        int limite = ehLogin ? loginTentativas : geralRequisicoes;
        long janelaMs = (ehLogin ? loginJanelaSegundos : geralJanelaSegundos) * 1000;
        String chave = request.getRemoteAddr() + (ehLogin ? ":login" : ":geral");

        long esperaSegundos = registrar(chave, limite, janelaMs);

        if (esperaSegundos > 0) {
            responderBloqueio(response, esperaSegundos, ehLogin);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private long registrar(String chave, int limite, long janelaMs) {
        long agora = System.currentTimeMillis();

        if (janelas.size() > LIMITE_ENTRADAS) {
            janelas.values().removeIf(janela -> agora - janela.inicioMs > janelaMs);
        }

        Janela janela = janelas.computeIfAbsent(chave, k -> new Janela(agora));

        synchronized (janela) {
            if (agora - janela.inicioMs > janelaMs) {
                janela.inicioMs = agora;
                janela.contador = 0;
            }

            janela.contador++;

            if (janela.contador > limite) {
                long restanteMs = janelaMs - (agora - janela.inicioMs);
                return Math.max(1, restanteMs / 1000);
            }
        }

        return 0;
    }

    private void responderBloqueio(HttpServletResponse response, long esperaSegundos, boolean ehLogin)
            throws IOException {
        String mensagem = ehLogin
                ? "Muitas tentativas de login. Aguarde " + esperaSegundos + " segundos e tente novamente."
                : "Muitas requisições em pouco tempo. Aguarde " + esperaSegundos + " segundos.";

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Retry-After", String.valueOf(esperaSegundos));

        ErroRespostaDTO corpo = new ErroRespostaDTO(
                HttpStatus.TOO_MANY_REQUESTS.value(), HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(), mensagem);
        response.getWriter().write(objectMapper.writeValueAsString(corpo));
    }

    private static final class Janela {
        private long inicioMs;
        private int contador;

        private Janela(long inicioMs) {
            this.inicioMs = inicioMs;
        }
    }
}
