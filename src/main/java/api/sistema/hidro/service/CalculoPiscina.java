package api.sistema.hidro.service;

import api.sistema.hidro.enums.DiametroPiscina;
import api.sistema.hidro.enums.SentidoTrecho;

/**
 * Dimensionamento do conjunto de recirculação de piscina e perda de carga trecho a trecho,
 * conforme a NBR 10339. Sem dependência de framework ou banco: recebe números, devolve números.
 */
public final class CalculoPiscina {

    /** Constante de Fair-Whipple-Hsiao para PVC, com o ajuste do memorial de origem. */
    private static final double FWH_CONSTANTE = 8.69e6;
    private static final double FWH_FATOR = 0.11;
    private static final double FWH_EXPOENTE_VAZAO = 1.75;
    private static final double FWH_EXPOENTE_DIAMETRO = 4.75;

    private static final double M3H_PARA_LS = 3.6;

    /** Vazão máxima que um bocal de retorno comporta (m³/h). */
    public static final double VAZAO_POR_BOCAL_M3H = 5.0;

    /** Área de superfície por bocal de retorno (m²), para a água circular por igual. */
    public static final int AREA_POR_BOCAL_M2 = 50;

    /** Área servida por ralo de fundo (m²). */
    public static final int AREA_POR_RALO_M2 = 50;

    public static final int AREA_POR_SKIMMER_PADRAO_M2 = 50;

    /** NBR 10339: no mínimo dois ralos interligados, por segurança. */
    public static final int MIN_RALOS = 2;

    /** Sem ralo de fundo, a sucção só por skimmer exige no mínimo dois. */
    public static final int MIN_SKIMMERS_SEM_RALO = 2;

    public static final int MIN_BOCAIS_RETORNO = 2;

    private CalculoPiscina() {
    }

    public record Dimensionamento(
            double areaM2,
            double volumeM3,
            double vazaoProjetoM3h,
            DiametroPiscina recalque,
            DiametroPiscina succao,
            double velocidadeRecalqueMs,
            double velocidadeSuccaoMs,
            double bocaisPorVazao,
            double bocaisPorArea,
            double bocaisCalculado,
            double skimmersCalculado,
            double ralosCalculado) {
    }

    public record Trecho(
            double vazaoM3h,
            DiametroPiscina diametro,
            double lEquivalenteM,
            double lRealM,
            double desnivelM,
            SentidoTrecho sentido) {
    }

    public record TrechoCalculado(
            double vazaoLs,
            double velocidadeMs,
            double perdaUnitariaMM,
            double lTotalM,
            double hfM,
            double pressaoMontanteMca,
            double pressaoJusanteMca) {
    }

    /**
     * @throws IllegalArgumentException se a bomba não atende a vazão de projeto — quem chama
     *                                  traduz para a mensagem de negócio
     */
    public static Dimensionamento dimensionar(double larguraM, double comprimentoM,
                                              double profundidadeM, int tempoFiltracaoH,
                                              double vazaoBombaM3h, int areaPorSkimmerM2) {
        double area = larguraM * comprimentoM;
        double volume = area * profundidadeM;
        double vazaoProjeto = volume / tempoFiltracaoH;

        if (vazaoBombaM3h < vazaoProjeto) {
            throw new IllegalArgumentException(String.format(
                    "vazão da bomba %.2f m³/h menor que a de projeto %.2f m³/h",
                    vazaoBombaM3h, vazaoProjeto));
        }

        DiametroPiscina recalque = DiametroPiscina.paraRecalque(vazaoBombaM3h);
        DiametroPiscina succao = DiametroPiscina.paraSuccao(vazaoBombaM3h);

        // Vale o critério que exigir mais bocais.
        double bocaisPorVazao = vazaoBombaM3h / VAZAO_POR_BOCAL_M3H;
        double bocaisPorArea = area / AREA_POR_BOCAL_M2;

        return new Dimensionamento(
                area,
                volume,
                vazaoProjeto,
                recalque,
                succao,
                recalque.velocidadeMs(vazaoBombaM3h),
                succao.velocidadeMs(vazaoBombaM3h),
                bocaisPorVazao,
                bocaisPorArea,
                Math.max(bocaisPorVazao, bocaisPorArea),
                area / areaPorSkimmerM2,
                area / AREA_POR_RALO_M2);
    }

    /** A pressão a jusante de um trecho é a de montante do seguinte. */
    public static TrechoCalculado calcularTrecho(Trecho trecho, double pressaoMontanteMca) {
        double vazaoLs = trecho.vazaoM3h() / M3H_PARA_LS;
        double perdaUnitaria = perdaUnitaria(vazaoLs, trecho.diametro().getDiametroInternoMm());
        double lTotal = trecho.lEquivalenteM() + trecho.lRealM();
        double hf = perdaUnitaria * lTotal;

        return new TrechoCalculado(
                vazaoLs,
                trecho.diametro().velocidadeMs(trecho.vazaoM3h()),
                perdaUnitaria,
                lTotal,
                hf,
                pressaoMontanteMca,
                trecho.sentido().pressaoJusante(pressaoMontanteMca, trecho.desnivelM(), hf));
    }

    /** Fair-Whipple-Hsiao para PVC, com Q em L/s e diâmetro interno em mm. */
    public static double perdaUnitaria(double vazaoLs, double diametroInternoMm) {
        return FWH_CONSTANTE
                * Math.pow(vazaoLs, FWH_EXPOENTE_VAZAO)
                / Math.pow(diametroInternoMm, FWH_EXPOENTE_DIAMETRO)
                * FWH_FATOR;
    }

    /** Arredonda para cima e respeita o mínimo, salvo quando o projetista fixa o valor. */
    public static int adotar(Integer override, double calculado, int minimo) {
        if (override != null) return override;
        return Math.max(minimo, (int) Math.ceil(calculado));
    }
}
