package api.sistema.hidro.service;

import java.util.List;

/**
 * Sumidouro cilíndrico: área de infiltração A = Cd / Tx, com Tx da Tabela A.1 da NBR 13969
 * pela taxa de percolação do solo. Fundo e parede lateral infiltram: A = πD²/4 + πD·h.
 */
public final class CalculoSumidouro {

    /** Linha da Tabela A.1 da NBR 13969: percolação (min/m) → taxa de aplicação (m³/m²·dia). */
    public record PontoTabela(double percolacaoMinM, double taxaAplicacao) {
    }

    /** Entre as linhas, interpola linearmente; até 40 min/m vale a primeira. */
    public static final List<PontoTabela> TABELA_A1 = List.of(
            new PontoTabela(40, 0.20),
            new PontoTabela(80, 0.14),
            new PontoTabela(120, 0.12),
            new PontoTabela(160, 0.10),
            new PontoTabela(200, 0.09),
            new PontoTabela(400, 0.065),
            new PontoTabela(600, 0.053),
            new PontoTabela(1200, 0.037),
            new PontoTabela(1400, 0.032),
            new PontoTabela(2400, 0.024));

    /** Diâmetro interno mínimo pedido pela NBR 17076:2024, em metros. */
    public static final double DIAMETRO_MINIMO_M = 1.0;

    /** Altura útil adotada em múltiplos de 0,05 m, como na geometria do tanque séptico. */
    private static final double PASSO_ALTURA_M = 0.05;
    private static final double LITROS_POR_M3 = 1000;

    private CalculoSumidouro() {
    }

    public record Resultado(
            double taxaAplicacao,
            double contribuicaoM3Dia,
            double areaTotalM2,
            double areaPorSumidouroM2,
            double areaFundoM2,
            double alturaTeoricaM,
            double alturaUtilM,
            double areaRealM2,
            double volumeUtilM3) {
    }

    /** @throws IllegalArgumentException quando a percolação passa do fim da Tabela A.1 */
    public static Resultado dimensionar(int contribuicaoDiariaLitros, double taxaPercolacao,
                                        double diametroM, int numSumidouros) {
        double taxaAplicacao = taxaAplicacao(taxaPercolacao);
        double contribuicaoM3 = contribuicaoDiariaLitros / LITROS_POR_M3;
        double areaTotal = contribuicaoM3 / taxaAplicacao;
        double areaPorSumidouro = areaTotal / numSumidouros;

        double areaFundo = Math.PI * diametroM * diametroM / 4;
        double alturaTeorica = (areaPorSumidouro - areaFundo) / (Math.PI * diametroM);
        double alturaUtil = Math.max(0, Math.ceil(arredondar(alturaTeorica / PASSO_ALTURA_M, 6)) * PASSO_ALTURA_M);

        return new Resultado(
                arredondar(taxaAplicacao, 4),
                arredondar(contribuicaoM3, 3),
                arredondar(areaTotal, 2),
                arredondar(areaPorSumidouro, 2),
                arredondar(areaFundo, 2),
                arredondar(alturaTeorica, 3),
                arredondar(alturaUtil, 2),
                arredondar(areaFundo + Math.PI * diametroM * alturaUtil, 2),
                arredondar(areaFundo * alturaUtil, 3));
    }

    public static double taxaAplicacao(double taxaPercolacao) {
        PontoTabela anterior = TABELA_A1.get(0);
        if (taxaPercolacao <= anterior.percolacaoMinM()) return anterior.taxaAplicacao();

        for (PontoTabela ponto : TABELA_A1.subList(1, TABELA_A1.size())) {
            if (taxaPercolacao <= ponto.percolacaoMinM()) {
                double fracao = (taxaPercolacao - anterior.percolacaoMinM())
                        / (ponto.percolacaoMinM() - anterior.percolacaoMinM());
                return anterior.taxaAplicacao() + fracao * (ponto.taxaAplicacao() - anterior.taxaAplicacao());
            }
            anterior = ponto;
        }
        throw new IllegalArgumentException(String.format(
                "Taxa de percolação de %.0f min/m passa do limite da Tabela A.1 da NBR 13969 "
                        + "(%.0f min/m): o solo não comporta infiltração por sumidouro.",
                taxaPercolacao, anterior.percolacaoMinM()));
    }

    private static double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }
}
