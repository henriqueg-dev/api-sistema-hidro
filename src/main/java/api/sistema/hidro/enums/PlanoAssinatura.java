package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Cálculos, orçamentos e memoriais em PDF são ilimitados em todo plano; o que varia é gente e IA. */
public enum PlanoAssinatura {

    STARTER("Starter", 7990, 2, 0),
    PROFISSIONAL("Profissional", 14990, 5, 300),
    ESCRITORIO("Escritório", 24990, -1, -1);

    private final String descricao;
    private final long precoCentavos;

    /** -1 = ilimitado. */
    private final int maxUsuarios;

    /** Mensagens do assistente de IA por mês; 0 = sem acesso, -1 = ilimitado. */
    private final int limiteMensagensAssistenteMes;

    PlanoAssinatura(String descricao, long precoCentavos, int maxUsuarios, int limiteMensagensAssistenteMes) {
        this.descricao = descricao;
        this.precoCentavos = precoCentavos;
        this.maxUsuarios = maxUsuarios;
        this.limiteMensagensAssistenteMes = limiteMensagensAssistenteMes;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    public long getPrecoCentavos() {
        return precoCentavos;
    }

    public int getMaxUsuarios() {
        return maxUsuarios;
    }

    public int getLimiteMensagensAssistenteMes() {
        return limiteMensagensAssistenteMes;
    }

    @JsonCreator
    public static PlanoAssinatura fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (PlanoAssinatura plano : values()) {
            if (plano.name().equalsIgnoreCase(valor) || plano.descricao.equalsIgnoreCase(valor)) {
                return plano;
            }
        }
        throw new IllegalArgumentException("Plano inválido: " + valor);
    }
}
