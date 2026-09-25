package api.sistema.hidro.controller;

import api.sistema.hidro.enums.PlanoAssinatura;
import api.sistema.hidro.service.AssinaturaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * AbacatePay assina o corpo com HMAC-SHA256 no cabeçalho X-Webhook-Signature. O secret é o
 * mesmo informado ao cadastrar o webhook no painel/API da AbacatePay (ver ABACATEPAY_WEBHOOK_SECRET).
 */
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    // Instância própria: o Spring Boot 4 deste projeto não expõe mais um bean ObjectMapper
    // do Jackson 2 clássico (com.fasterxml) por padrão — só precisamos ler alguns campos aqui.
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AssinaturaService assinaturaService;

    @Value("${abacatepay.webhook-secret:}")
    private String webhookSecret;

    @PostMapping("/abacatepay")
    public ResponseEntity<Void> receber(@RequestBody String corpo,
                                        @RequestHeader(value = "X-Webhook-Signature", required = false) String assinatura)
            throws IOException {
        if (webhookSecret.isBlank() || !assinaturaValida(corpo, assinatura)) {
            log.warn("Webhook da AbacatePay recusado: assinatura inválida ou secret não configurado");
            return ResponseEntity.status(401).build();
        }

        // Deixa qualquer falha subir pro GlobalExceptionHandler (loga e devolve 500) em vez de
        // engolir o erro e responder 200 — a AbacatePay só reentrega quando a resposta não é 2xx.
        JsonNode raiz = OBJECT_MAPPER.readTree(corpo);
        JsonNode dados = raiz.path("data");

        if (raiz.path("event").asText("").equals("transparent.completed")) {
            String cobrancaId = dados.path("id").asText(null);
            Long contaId = dados.path("metadata").path("contaId").asLong(0);
            PlanoAssinatura plano = planoDoMetadata(dados.path("metadata").path("plano").asText(""));
            if (cobrancaId != null && contaId > 0) {
                assinaturaService.confirmarPagamento(contaId, cobrancaId, plano);
            }
        }

        return ResponseEntity.ok().build();
    }

    // Plano desconhecido não pode derrubar a confirmação de um pagamento real.
    private PlanoAssinatura planoDoMetadata(String valor) {
        try {
            return valor.isBlank() ? null : PlanoAssinatura.valueOf(valor);
        } catch (IllegalArgumentException e) {
            log.warn("Plano desconhecido no metadata do webhook: {}", valor);
            return null;
        }
    }

    private boolean assinaturaValida(String corpo, String assinaturaRecebida) {
        if (assinaturaRecebida == null) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] calculado = mac.doFinal(corpo.getBytes(StandardCharsets.UTF_8));
            String calculadoBase64 = Base64.getEncoder().encodeToString(calculado);
            return MessageDigest.isEqual(
                    calculadoBase64.getBytes(StandardCharsets.UTF_8),
                    assinaturaRecebida.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Falha ao validar assinatura do webhook", e);
            return false;
        }
    }
}
