package api.sistema.hidro.assistente.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import api.sistema.hidro.exception.RegraNegocioException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Cria o cliente da API do Claude sob demanda.
 *
 * <p>A chave nunca vai para o frontend: ela é lida da variável de ambiente
 * ANTHROPIC_API_KEY (ver application.properties). O cliente só é construído na
 * primeira pergunta, então a aplicação sobe normalmente mesmo sem a chave
 * configurada — quem tentar usar o assistente recebe uma mensagem clara.
 */
@Component
public class ClaudeClientProvider {

    private final String apiKey;

    /** Modelo usado nas respostas. Trocável por properties, sem recompilar. */
    @Getter
    private final String modelo;

    private volatile AnthropicClient client;

    public ClaudeClientProvider(
            @Value("${anthropic.api-key:}") String apiKey,
            @Value("${anthropic.modelo:claude-sonnet-5}") String modelo) {
        this.apiKey = apiKey;
        this.modelo = modelo;
    }

    public boolean configurado() {
        return apiKey != null && !apiKey.isBlank();
    }

    public AnthropicClient obter() {
        if (!configurado()) {
            throw new RegraNegocioException(
                    "A chave da API do Claude não está configurada. "
                            + "Defina a variável de ambiente ANTHROPIC_API_KEY e reinicie a aplicação.");
        }

        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = AnthropicOkHttpClient.builder().apiKey(apiKey).build();
                }
            }
        }

        return client;
    }
}
