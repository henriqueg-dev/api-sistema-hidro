package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CondicaoSanca {
    SEM_SANCA("Sem sanca"),
    ATE_1_2("Até 1,2m"),
    ENTRE_1_2_E_1_8("Entre 1,2m e 1,8m"),
    ACIMA_1_8("Acima de 1,8m");

    private final String descricao;

    CondicaoSanca(String descricao) {
        this.descricao = descricao;
    }

    /** Valor enviado nas respostas da API: "Entre 1,2m e 1,8m" em vez de ENTRE_1_2_E_1_8. */
    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    /** Nas requisições aceita tanto a constante (ENTRE_1_2_E_1_8) quanto a descrição. */
    @JsonCreator
    public static CondicaoSanca fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (CondicaoSanca condicao : values()) {
            if (condicao.name().equalsIgnoreCase(valor) || condicao.descricao.equalsIgnoreCase(valor)) {
                return condicao;
            }
        }
        throw new IllegalArgumentException("Condição da sanca inválida: " + valor);
    }
}
