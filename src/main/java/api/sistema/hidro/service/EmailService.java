package api.sistema.hidro.service;

import api.sistema.hidro.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestClient restClient;
    private final TemplateEngine templateEngine;
    private final String remetente;
    private final boolean configurado;

    public EmailService(TemplateEngine templateEngine,
                        @Value("${resend.api-key:}") String apiKey,
                        @Value("${resend.from}") String remetente) {
        this.templateEngine = templateEngine;
        this.remetente = remetente;
        this.configurado = !apiKey.isBlank();
        this.restClient = RestClient.builder()
                .baseUrl("https://api.resend.com")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public void enviarConvite(String destinatario, String nomeConvidado, String codigo, String link) {
        Context contexto = new Context();
        contexto.setVariable("nome", nomeConvidado);
        contexto.setVariable("codigo", codigo);
        contexto.setVariable("link", link);
        enviar(destinatario, "Você foi convidado para o SistemaHidro", "email/convite", contexto);
    }

    public void enviarCodigoRecuperacao(String destinatario, String nome, String codigo) {
        Context contexto = new Context();
        contexto.setVariable("nome", nome);
        contexto.setVariable("codigo", codigo);
        enviar(destinatario, "Código para redefinir sua senha", "email/recuperacao-senha", contexto);
    }

    private void enviar(String destinatario, String assunto, String template, Context contexto) {
        if (!configurado) {
            throw new RegraNegocioException("Envio de e-mail não configurado no servidor");
        }

        String html = templateEngine.process(template, contexto);

        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "from", remetente,
                            "to", destinatario,
                            "subject", assunto,
                            "html", html))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail via Resend para {}", destinatario, e);
            throw new RegraNegocioException("Não foi possível enviar o e-mail");
        }
    }
}
