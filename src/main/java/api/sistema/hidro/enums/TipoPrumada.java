package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoPrumada {
    COZINHA("Cozinha"),
    ARS("ARS - Área de serviço");

    private final String descricao;

    TipoPrumada(String descricao) {
        this.descricao = descricao;
    }

    /** Valor enviado nas respostas da API. */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /** Nas requisições aceita tanto a constante (COZINHA) quanto a descrição. */
    @JsonCreator
    public static TipoPrumada fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (TipoPrumada tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor) || tipo.descricao.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de prumada inválido: " + valor);
    }
}
