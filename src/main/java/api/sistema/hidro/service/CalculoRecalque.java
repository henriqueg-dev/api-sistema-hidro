package api.sistema.hidro.service;

import api.sistema.hidro.enums.DiametroRamal;
import api.sistema.hidro.enums.TipoConexao;

import java.util.List;

/**
 * Instalação elevatória do reservatório inferior ao superior.
 *
 * <p>A vazão sai do consumo diário e das horas de funcionamento da bomba. O diâmetro de
 * recalque vem da fórmula de Forchheimer, D = 1,3 · (X/24)^¼ · √Q, e a sucção adota o diâmetro
 * comercial seguinte. As perdas são de Fair-Whipple-Hsiao (NBR 5626) e a potência é
 * P = Q · Hman / (75 · η).
 */
public final class CalculoRecalque {

    public static final double FORCHHEIMER_C = 1.3;
    private static final double HORAS_POR_DIA = 24;

    /** Critério usual: a bomba repõe ao menos 15% do consumo diário por hora. */
    public static final double FRACAO_MINIMA_HORARIA = 0.15;

    /** Velocidade máxima admitida pela NBR 5626 em qualquer trecho, em m/s. */
    public static final double VELOCIDADE_MAXIMA_MS = 3.0;

    /** Fair-Whipple-Hsiao para PVC na NBR 5626: J em kPa/m, com Q em L/s e d em mm. */
    private static final double FWH_CONSTANTE_KPA = 8.69e6;
    private static final double FWH_EXPOENTE_VAZAO = 1.75;
    private static final double FWH_EXPOENTE_DIAMETRO = 4.75;
    private static final double KPA_POR_MCA = 10;

    private static final double GRAVIDADE = 9.81;
    private static final int DENOMINADOR_CV = 75;
    private static final double KW_POR_CV = 0.7355;
    private static final int SEGUNDOS_POR_HORA = 3600;
    private static final double LITROS_POR_M3 = 1000;

    /** A tabela de comprimentos equivalentes das conexões começa em DN 25. */
    private static final int DN_MINIMO = 25;

    /** Margem sobre a potência calculada por faixa, em cv: prática de projeto, não norma. */
    public static final double[][] FOLGAS = {{2, 50}, {5, 30}, {10, 20}, {20, 15}};
    public static final int FOLGA_ACIMA = 10;

    /** Potências nominais usuais de motores elétricos, em cv. */
    public static final double[] MOTORES_CV =
            {0.25, 0.33, 0.5, 0.75, 1, 1.5, 2, 3, 4, 5, 6, 7.5, 10, 12.5, 15, 20, 25, 30, 40, 50};

    private CalculoRecalque() {
    }

    public record Conexao(TipoConexao tipo, int quantidade) {
    }

    public record Trecho(
            DiametroRamal diametro,
            double velocidadeMs,
            double perdaUnitaria,
            double comprimentoEquivalenteM,
            double perdaCargaM) {
    }

    public record Resultado(
            int populacao,
            double consumoDiarioLitros,
            double vazaoLs,
            double vazaoM3h,
            double horasMaximas,
            double diametroTeoricoMm,
            Trecho succao,
            Trecho recalque,
            double cargaVelocidadeM,
            double alturaManometricaM,
            double potenciaCv,
            double potenciaKw,
            int folgaPercentual,
            double potenciaComFolgaCv,
            Double motorComercialCv) {
    }

    /**
     * @throws IllegalArgumentException quando a vazão pede diâmetro acima da tabela ou os
     *                                  desníveis resultam em altura manométrica não positiva
     */
    public static Resultado dimensionar(int taxaOcupacao, int numUnidades, int consumoPerCapita,
                                        double horasFuncionamento,
                                        double desnivelSuccao, double comprimentoSuccao,
                                        List<Conexao> conexoesSuccao,
                                        double desnivelRecalque, double comprimentoRecalque,
                                        List<Conexao> conexoesRecalque,
                                        int rendimentoPercentual) {
        int populacao = taxaOcupacao * numUnidades;
        double consumoDiarioLitros = (double) populacao * consumoPerCapita;
        double vazaoLs = consumoDiarioLitros / (horasFuncionamento * SEGUNDOS_POR_HORA);
        double vazaoM3s = vazaoLs / LITROS_POR_M3;

        double diametroTeoricoMm = FORCHHEIMER_C
                * Math.pow(horasFuncionamento / HORAS_POR_DIA, 0.25)
                * Math.sqrt(vazaoM3s) * LITROS_POR_M3;

        DiametroRamal dnRecalque = diametroRecalque(diametroTeoricoMm);
        DiametroRamal dnSuccao = diametroSeguinte(dnRecalque);

        Trecho succao = trecho(dnSuccao, vazaoLs, comprimentoSuccao, conexoesSuccao);
        Trecho recalque = trecho(dnRecalque, vazaoLs, comprimentoRecalque, conexoesRecalque);

        double cargaVelocidade = Math.pow(recalque.velocidadeMs(), 2) / (2 * GRAVIDADE);
        double alturaManometrica = desnivelSuccao + desnivelRecalque
                + succao.perdaCargaM() + recalque.perdaCargaM() + cargaVelocidade;

        if (alturaManometrica <= 0) {
            throw new IllegalArgumentException(
                    "Os desníveis informados resultam em altura manométrica nula ou negativa.");
        }

        double potenciaCv = vazaoLs * alturaManometrica / (DENOMINADOR_CV * rendimentoPercentual / 100.0);
        int folga = folga(potenciaCv);
        double potenciaComFolga = potenciaCv * (1 + folga / 100.0);

        return new Resultado(
                populacao,
                consumoDiarioLitros,
                arredondar(vazaoLs, 4),
                arredondar(vazaoLs * SEGUNDOS_POR_HORA / LITROS_POR_M3, 3),
                arredondar(1 / FRACAO_MINIMA_HORARIA, 2),
                arredondar(diametroTeoricoMm, 2),
                succao,
                recalque,
                arredondar(cargaVelocidade, 3),
                arredondar(alturaManometrica, 2),
                arredondar(potenciaCv, 3),
                arredondar(potenciaCv * KW_POR_CV, 3),
                folga,
                arredondar(potenciaComFolga, 3),
                motorComercial(potenciaComFolga));
    }

    /** Menor diâmetro comercial, a partir de DN 25, com interno igual ou maior que o teórico. */
    private static DiametroRamal diametroRecalque(double diametroTeoricoMm) {
        for (DiametroRamal diametro : DiametroRamal.values()) {
            if (diametro.getDn() >= DN_MINIMO && diametro.getDiametroInternoMm() >= diametroTeoricoMm) {
                return diametro;
            }
        }
        throw new IllegalArgumentException(String.format(
                "O diâmetro teórico de recalque (%.1f mm) passa do maior tubo da tabela (DN 110).",
                diametroTeoricoMm));
    }

    private static DiametroRamal diametroSeguinte(DiametroRamal recalque) {
        DiametroRamal[] diametros = DiametroRamal.values();
        int indice = recalque.ordinal() + 1;
        if (indice >= diametros.length) {
            throw new IllegalArgumentException(
                    "O recalque já usa o maior tubo da tabela (DN 110); não há diâmetro acima para a sucção.");
        }
        return diametros[indice];
    }

    private static Trecho trecho(DiametroRamal diametro, double vazaoLs, double comprimentoReal,
                                 List<Conexao> conexoes) {
        double comprimentoEquivalente = conexoes.stream()
                .mapToDouble(c -> c.quantidade() * c.tipo().comprimentoEquivalenteM(diametro.getDn()))
                .sum();
        double perdaUnitaria = perdaUnitaria(vazaoLs, diametro.getDiametroInternoMm());

        return new Trecho(
                diametro,
                arredondar(diametro.velocidadeMs(vazaoLs), 3),
                arredondar(perdaUnitaria, 5),
                arredondar(comprimentoEquivalente, 2),
                arredondar(perdaUnitaria * (comprimentoReal + comprimentoEquivalente), 3));
    }

    /** Perda de carga unitária em m/m. */
    public static double perdaUnitaria(double vazaoLs, double diametroInternoMm) {
        return FWH_CONSTANTE_KPA
                * Math.pow(vazaoLs, FWH_EXPOENTE_VAZAO)
                * Math.pow(diametroInternoMm, -FWH_EXPOENTE_DIAMETRO)
                / KPA_POR_MCA;
    }

    public static int folga(double potenciaCv) {
        for (double[] faixa : FOLGAS) {
            if (potenciaCv <= faixa[0]) return (int) faixa[1];
        }
        return FOLGA_ACIMA;
    }

    /** Menor motor usual que cobre a potência; nulo acima do maior da lista. */
    private static Double motorComercial(double potenciaCv) {
        for (double motor : MOTORES_CV) {
            if (motor >= potenciaCv) return motor;
        }
        return null;
    }

    private static double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }
}
