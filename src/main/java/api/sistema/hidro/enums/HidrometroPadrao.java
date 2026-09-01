package api.sistema.hidro.enums;

/**
 * Vazões nominais de hidrômetro padronizadas pela ABNT NBR 14005 / ISO 4064, em m³/h.
 * A vazão máxima é o dobro da nominal.
 *
 * <p>A faixa de vazão é comum a todas as concessionárias, mas a regra de escolha não: cada
 * prestadora tem sua tabela, em geral por consumo mensal. O resultado aqui é uma sugestão a
 * confirmar com a concessionária do empreendimento.
 */
public enum HidrometroPadrao {

    QN_0_75(0.75),
    QN_1_5(1.5),
    QN_2_5(2.5),
    QN_3_5(3.5),
    QN_5(5.0),
    QN_7(7.0),
    QN_10(10.0),
    QN_15(15.0),
    QN_20(20.0),
    QN_30(30.0);

    private static final int FATOR_VAZAO_MAXIMA = 2;

    private final double vazaoNominalM3h;

    HidrometroPadrao(double vazaoNominalM3h) {
        this.vazaoNominalM3h = vazaoNominalM3h;
    }

    public double getVazaoNominalM3h() {
        return vazaoNominalM3h;
    }

    public double getVazaoMaximaM3h() {
        return vazaoNominalM3h * FATOR_VAZAO_MAXIMA;
    }

    /** Menor medidor cuja vazão nominal cobre a vazão de projeto. */
    public static HidrometroPadrao menorPara(double vazaoProjetoM3h) {
        for (HidrometroPadrao hidrometro : values()) {
            if (hidrometro.vazaoNominalM3h >= vazaoProjetoM3h) {
                return hidrometro;
            }
        }
        return QN_30;
    }
}
