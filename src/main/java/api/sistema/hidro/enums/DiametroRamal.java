package api.sistema.hidro.enums;

/**
 * Tubo de PVC soldável: diâmetro nominal e interno, em mm. Os DN de 20 a 40 vêm do catálogo
 * do fabricante; de 50 a 110 repetem a tabela usada no cálculo de piscina.
 */
public enum DiametroRamal {

    DN_20(20, 16.6),
    DN_25(25, 21.1),
    DN_32(32, 27.2),
    DN_40(40, 34.0),
    DN_50(50, 44.0),
    DN_60(60, 53.4),
    DN_75(75, 66.6),
    DN_85(85, 75.6),
    DN_110(110, 97.8);

    private final int dn;
    private final double diametroInternoMm;

    DiametroRamal(int dn, double diametroInternoMm) {
        this.dn = dn;
        this.diametroInternoMm = diametroInternoMm;
    }

    public int getDn() {
        return dn;
    }

    public double getDiametroInternoMm() {
        return diametroInternoMm;
    }

    public double velocidadeMs(double vazaoLs) {
        double raioM = diametroInternoMm / 1000.0 / 2.0;
        return (vazaoLs / 1000.0) / (Math.PI * raioM * raioM);
    }

    /** Menor diâmetro comercial em que a água não passa da velocidade limite. */
    public static DiametroRamal menorPara(double vazaoLs, double velocidadeMaximaMs) {
        for (DiametroRamal diametro : values()) {
            if (diametro.velocidadeMs(vazaoLs) <= velocidadeMaximaMs) {
                return diametro;
            }
        }
        return DN_110;
    }
}
