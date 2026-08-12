package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FaixaPavimentos {
    ATE_5("Até 5 pavimentos"),
    ATE_9("Até 9 pavimentos"),
    ATE_16("Até 16 pavimentos"),
    ATE_18("Até 18 pavimentos"),
    ACIMA_18("Acima de 18 pavimentos");

    private final String descricao;

    FaixaPavimentos(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static FaixaPavimentos fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (FaixaPavimentos faixa : values()) {
            if (faixa.name().equalsIgnoreCase(valor) || faixa.descricao.equalsIgnoreCase(valor)) {
                return faixa;
            }
        }
        throw new IllegalArgumentException("Faixa de pavimentos inválida: " + valor);
    }

    public static FaixaPavimentos resolver(TipoPrumada tipo, int numPavimentos) {
        if (numPavimentos <= 5 && tipo == TipoPrumada.ARS) return ATE_5;
        if (numPavimentos <= 9) return ATE_9;
        if (numPavimentos <= 16) return ATE_16;
        if (numPavimentos <= 18) return ATE_18;
        return ACIMA_18;
    }
}
