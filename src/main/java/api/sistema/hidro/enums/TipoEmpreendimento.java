package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TipoEmpreendimento {
    CASA("Casa"),
    PREDIO("Prédio"),
    GALPAO("Galpão");

    private final String descricao;

    TipoEmpreendimento(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoEmpreendimento fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (TipoEmpreendimento tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor) || tipo.descricao.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de empreendimento inválido: " + valor);
    }
}
