package api.sistema.hidro.enums;

/**
 * Faixa de temperatura ambiente do mês mais frio, usada na Tabela 3 da NBR 7229 para obter a
 * taxa de acumulação total de lodo (K).
 */
public enum FaixaTemperatura {

    ATE_10(94),
    DE_10_A_20(65),
    ACIMA_20(57);

    /** K da Tabela 3 para intervalo de limpeza de 1 ano, em dias. */
    private final int taxaUmAno;

    /**
     * A cada ano a mais de intervalo entre limpezas, a Tabela 3 acrescenta 40 dias em todas as
     * três faixas de temperatura — por isso a progressão substitui a tabela inteira aqui.
     */
    private static final int ACRESCIMO_POR_ANO = 40;

    FaixaTemperatura(int taxaUmAno) {
        this.taxaUmAno = taxaUmAno;
    }

    /**
     * K — taxa de acumulação total de lodo, em dias.
     *
     * @param intervaloLimpezaAnos intervalo entre limpezas, de 1 a 5 anos (limites da Tabela 3)
     */
    public int taxaAcumulacaoLodo(int intervaloLimpezaAnos) {
        return taxaUmAno + ACRESCIMO_POR_ANO * (intervaloLimpezaAnos - 1);
    }
}
