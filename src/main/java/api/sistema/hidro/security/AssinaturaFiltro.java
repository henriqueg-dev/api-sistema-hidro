package api.sistema.hidro.security;

import api.sistema.hidro.catalogo.entity.AssinaturaEntity;
import api.sistema.hidro.catalogo.repository.AssinaturaRepository;
import api.sistema.hidro.enums.StatusAssinatura;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/** Conta sem assinatura ativa só acessa login e a própria tela de assinatura/pagamento. */
@Component
@RequiredArgsConstructor
public class AssinaturaFiltro extends OncePerRequestFilter {

    private static final Set<String> PREFIXOS_LIVRES = Set.of("/api/auth", "/api/webhooks", "/api/assinatura");

    private final AssinaturaRepository assinaturaRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String caminho = request.getRequestURI();
        if (PREFIXOS_LIVRES.stream().anyMatch(caminho::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            // Sem usuário autenticado: quem barra é o AuthorizationFilter do Spring Security, com 401.
            filterChain.doFilter(request, response);
            return;
        }

        StatusAssinatura status = assinaturaRepository.findById(usuario.getContaId())
                .map(AssinaturaEntity::statusEfetivo)
                .orElse(StatusAssinatura.EXPIRADA);

        if (status != StatusAssinatura.ATIVA) {
            response.setStatus(402);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"status":402,"erro":"Payment Required",\
                    "mensagem":"Assinatura expirada ou pendente. Regularize o pagamento para continuar."}""");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
