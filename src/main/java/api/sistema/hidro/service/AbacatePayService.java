package api.sistema.hidro.service;

import api.sistema.hidro.enums.PlanoAssinatura;
import api.sistema.hidro.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.text.Normalizer;
import java.util.Map;

/**
 * Checkout Transparente (PIX) da AbacatePay. Sem assinatura recorrente nativa — a cobrança é
 * gerada sob demanda a cada renovação e confirmada por webhook (ver {@link AssinaturaService}).
 */
@Service
public class AbacatePayService {

    private static final Logger log = LoggerFactory.getLogger(AbacatePayService.class);

    private final RestClient restClient;
    private final boolean configurado;

    public AbacatePayService(@Value("${abacatepay.api-key:}") String apiKey) {
        this.configurado = !apiKey.isBlank();
        this.restClient = RestClient.builder()
                .baseUrl("https://api.abacatepay.com/v2")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    // A AbacatePay recusa caracteres fora do ASCII na descrição (ex.: "—"); "Escritório" vira "Escritorio".
    private static String semAcentos(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    public record CobrancaPix(String id, String brCode, String brCodeBase64) {
    }

    /** Gera um PIX Checkout Transparente; o plano vai no metadata e volta no webhook. */
    @SuppressWarnings("unchecked")
    public CobrancaPix criarCobranca(long contaId, PlanoAssinatura plano) {
        if (!configurado) {
            throw new RegraNegocioException("Pagamento não configurado no servidor");
        }

        try {
            Map<String, Object> resposta = restClient.post()
                    .uri("/transparents/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "method", "PIX",
                            "data", Map.of(
                                    "amount", plano.getPrecoCentavos(),
                                    "description", "Assinatura Hidros - plano " + semAcentos(plano.getDescricao()),
                                    "metadata", Map.of(
                                            "contaId", String.valueOf(contaId),
                                            "plano", plano.name()))))
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> dados = (Map<String, Object>) resposta.get("data");
            String cobrancaId = (String) dados.get("id");
            log.info("Cobrança PIX {} criada na AbacatePay para conta {}", cobrancaId, contaId);
            return new CobrancaPix(cobrancaId, (String) dados.get("brCode"), (String) dados.get("brCodeBase64"));
        } catch (Exception e) {
            log.error("Falha ao criar cobrança PIX na AbacatePay para conta {}", contaId, e);
            throw new RegraNegocioException("Não foi possível gerar a cobrança agora. Tente novamente.");
        }
    }
}
