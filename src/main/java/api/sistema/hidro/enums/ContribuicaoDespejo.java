package api.sistema.hidro.enums;

/**
 * Tabela 1 da NBR 7229, mantida pela NBR 17076: contribuição diária de esgoto (C) e de lodo
 * fresco (Lf) por tipo de prédio e de ocupante, ambas em litros por unidade e por dia.
 *
 * <p>A unidade de contribuição não é sempre uma pessoa: restaurante conta refeições, cinema
 * conta lugares e sanitário público conta bacias sanitárias. É esse valor que entra como N na
 * fórmula do volume útil.
 */
public enum ContribuicaoDespejo {

    RESIDENCIA_PADRAO_ALTO(160, 1.0, UnidadeContribuicao.PESSOA),
    RESIDENCIA_PADRAO_MEDIO(130, 1.0, UnidadeContribuicao.PESSOA),
    RESIDENCIA_PADRAO_BAIXO(100, 1.0, UnidadeContribuicao.PESSOA),
    HOTEL(100, 1.0, UnidadeContribuicao.PESSOA),
    ALOJAMENTO_PROVISORIO(80, 1.0, UnidadeContribuicao.PESSOA),
    FABRICA(70, 0.30, UnidadeContribuicao.PESSOA),
    ESCRITORIO(50, 0.20, UnidadeContribuicao.PESSOA),
    EDIFICIO_PUBLICO_COMERCIAL(50, 0.20, UnidadeContribuicao.PESSOA),
    ESCOLA(50, 0.20, UnidadeContribuicao.PESSOA),
    RESTAURANTE(25, 0.10, UnidadeContribuicao.REFEICAO),
    BAR(6, 0.10, UnidadeContribuicao.PESSOA),
    CINEMA_TEATRO(2, 0.02, UnidadeContribuicao.LUGAR),
    SANITARIO_PUBLICO(480, 4.0, UnidadeContribuicao.BACIA_SANITARIA);

    private final int contribuicaoLitros;
    private final double lodoFrescoLitros;
    private final UnidadeContribuicao unidade;

    ContribuicaoDespejo(int contribuicaoLitros, double lodoFrescoLitros, UnidadeContribuicao unidade) {
        this.contribuicaoLitros = contribuicaoLitros;
        this.lodoFrescoLitros = lodoFrescoLitros;
        this.unidade = unidade;
    }

    /** C — contribuição de esgoto, em L/unidade.dia. */
    public int getContribuicaoLitros() {
        return contribuicaoLitros;
    }

    /** Lf — contribuição de lodo fresco, em L/unidade.dia. */
    public double getLodoFrescoLitros() {
        return lodoFrescoLitros;
    }

    /** O que N conta neste tipo de prédio. */
    public UnidadeContribuicao getUnidade() {
        return unidade;
    }
}
