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

    /** Valor enviado nas respostas da API: "Até 9 pavimentos" em vez de ATE_9. */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /** Nas requisições aceita tanto a constante (ATE_9) quanto a descrição. */
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

    /**
     * Converte o número de pavimentos informado na consulta na faixa normativa
     * correspondente. Prumadas de cozinha não possuem faixa própria até 5 pavimentos.
     */
    public static FaixaPavimentos resolver(TipoPrumada tipo, int numPavimentos) {
        if (numPavimentos <= 5 && tipo == TipoPrumada.ARS) return ATE_5;
        if (numPavimentos <= 9) return ATE_9;
        if (numPavimentos <= 16) return ATE_16;
        if (numPavimentos <= 18) return ATE_18;
        return ACIMA_18;
    }
}
