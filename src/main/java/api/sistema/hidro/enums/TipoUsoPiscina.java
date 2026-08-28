package api.sistema.hidro.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** Tabela 1 da NBR 10339: tempo máximo de filtração (h) por tipologia e profundidade. */
public enum TipoUsoPiscina {

    RESIDENCIAL_PRIVATIVA("Residencial privativa", 4, 8, 8),
    PUBLICA_COLETIVA("Pública / coletiva / hospedaria", 2, 6, 8),
    OCUPACAO_ALTA("Ocupação acima de 1 usuário/2 m² em 12 h", 2, 4, 6);

    private final String descricao;
    private final int ateZeroSeis;
    private final int deZeroSeisAUmCinco;
    private final int acimaUmCinco;

    TipoUsoPiscina(String descricao, int ateZeroSeis, int deZeroSeisAUmCinco, int acimaUmCinco) {
        this.descricao = descricao;
        this.ateZeroSeis = ateZeroSeis;
        this.deZeroSeisAUmCinco = deZeroSeisAUmCinco;
        this.acimaUmCinco = acimaUmCinco;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static TipoUsoPiscina fromJson(String valor) {
        if (valor == null || valor.isBlank()) return null;
        for (TipoUsoPiscina tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor) || tipo.descricao.equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de uso da piscina inválido: " + valor);
    }

    /** Tempo máximo de filtração (h) admitido pela Tabela 1 para esta profundidade. */
    public int tempoMaximoFiltracaoH(double profundidadeM) {
        if (profundidadeM <= 0.60) return ateZeroSeis;
        if (profundidadeM <= 1.50) return deZeroSeisAUmCinco;
        return acimaUmCinco;
    }

    /** Residencial privativa pode usar renovação de 3x/dia no lugar do tempo tabelado. */
    public boolean admiteRenovacaoDiaria() {
        return this == RESIDENCIAL_PRIVATIVA;
    }
}
