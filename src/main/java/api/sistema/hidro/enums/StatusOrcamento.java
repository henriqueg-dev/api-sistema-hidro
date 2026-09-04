package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusOrcamento {
    RASCUNHO("Rascunho"),
    ENVIADO("Enviado"),
    APROVADO("Aprovado"),
    RECUSADO("Recusado");

    private final String descricao;

    StatusOrcamento(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusOrcamento fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (StatusOrcamento status : values()) {
            if (status.name().equalsIgnoreCase(valor) || status.descricao.equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de orçamento inválido: " + valor);
    }
}
