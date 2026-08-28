package api.sistema.hidro.enums;

import api.sistema.hidro.exception.RegraNegocioException;

/**
 * Tubo de PVC soldável classe 15: DN comercial, diâmetro interno e seleção por vazão.
 * As faixas de DN aproximam os limites da Tabela 3 da NBR 10339, mas estouram um pouco
 * no topo de algumas — por isso o serviço recalcula a velocidade real e alerta.
 */
public enum DiametroPiscina {

    DN_50(50, 44.0),
    DN_60(60, 53.4),
    DN_75(75, 66.6),
    DN_85(85, 75.6),
    DN_110(110, 97.8);

    /** Velocidade máxima no conjunto de sucção (m/s) — Tabela 3 da NBR 10339. */
    public static final double VELOCIDADE_MAX_SUCCAO = 1.8;

    /** Velocidade máxima no conjunto de recalque (m/s) — Tabela 3 da NBR 10339. */
    public static final double VELOCIDADE_MAX_RECALQUE = 3.0;

    private final int dn;
    private final double diametroInternoMm;

    DiametroPiscina(int dn, double diametroInternoMm) {
        this.dn = dn;
        this.diametroInternoMm = diametroInternoMm;
    }

    public int getDn() {
        return dn;
    }

    public double getDiametroInternoMm() {
        return diametroInternoMm;
    }

    public static DiametroPiscina porDn(int dn) {
        for (DiametroPiscina diametro : values()) {
            if (diametro.dn == dn) return diametro;
        }
        throw new RegraNegocioException(
                "Diâmetro nominal não suportado nos trechos: DN " + dn
                        + ". Use 50, 60, 75, 85 ou 110 mm.");
    }

    /** Seleção do DN do conjunto de recalque (retorno) a partir da vazão da bomba. */
    public static DiametroPiscina paraRecalque(double vazaoM3h) {
        if (vazaoM3h < 15) return DN_50;
        if (vazaoM3h < 25) return DN_60;
        if (vazaoM3h < 35) return DN_75;
        if (vazaoM3h < 53) return DN_85;
        if (vazaoM3h <= 80) return DN_110;
        throw new RegraNegocioException(
                "Limite de vazão excedido para o conjunto de recalque: " + vazaoM3h
                        + " m³/h. A tabela de diâmetros vai até 80 m³/h (DN 110).");
    }

    /** Seleção do DN do conjunto de sucção — vale para ralo, aspiração e skimmer. */
    public static DiametroPiscina paraSuccao(double vazaoM3h) {
        if (vazaoM3h < 9) return DN_50;
        if (vazaoM3h < 15) return DN_60;
        if (vazaoM3h < 21) return DN_75;
        if (vazaoM3h < 32) return DN_85;
        if (vazaoM3h <= 50) return DN_110;
        throw new RegraNegocioException(
                "Limite de vazão excedido para o conjunto de sucção: " + vazaoM3h
                        + " m³/h. A tabela de diâmetros vai até 50 m³/h (DN 110).");
    }

    /** Velocidade média do escoamento (m/s) para uma vazão em m³/h neste diâmetro. */
    public double velocidadeMs(double vazaoM3h) {
        double vazaoM3s = vazaoM3h / 3600.0;
        double raioM = diametroInternoMm / 1000.0 / 2.0;
        return vazaoM3s / (Math.PI * raioM * raioM);
    }
}
