package api.sistema.hidro.service;

import api.sistema.hidro.enums.FormaTanque;
import api.sistema.hidro.exception.RegraNegocioException;

import java.util.Optional;

/**
 * Resolve a geometria interna do tanque séptico a partir do volume útil já calculado.
 *
 * <p>Toda a busca por dimensões acontece em centímetros inteiros: as dimensões precisam ser
 * múltiplos de 0,05 m e a comparação de área é exata, o que em ponto flutuante seria frágil.
 */
public final class GeometriaTanqueSeptico {

    /** As dimensões internas são adotadas em múltiplos de 0,05 m. */
    private static final int PASSO_CM = 5;

    private static final int LARGURA_MINIMA_CM = 80;
    private static final int DIAMETRO_MINIMO_CM = 110;

    /** Relação comprimento/largura admitida no tanque prismático retangular. */
    private static final int RELACAO_MINIMA = 2;
    private static final int RELACAO_MAXIMA = 4;

    /** Câmara de gases acrescida à profundidade útil; não integra o volume útil. */
    public static final int CAMARA_GASES_CM = 30;

    private GeometriaTanqueSeptico() {
    }

    /**
     * Faixa de profundidade útil admitida, em centímetros, conforme o volume útil do tanque.
     */
    public record Faixa(int minimoCm, int maximoCm) {

        public static Faixa para(double volumeUtilM3) {
            if (volumeUtilM3 <= 6.0) return new Faixa(120, 220);
            if (volumeUtilM3 <= 10.0) return new Faixa(150, 250);
            return new Faixa(180, 280);
        }

        public double minimoM() {
            return minimoCm / 100.0;
        }

        public double maximoM() {
            return maximoCm / 100.0;
        }
    }

    /**
     * Geometria adotada. Largura, comprimento e relação valem para o prismático retangular;
     * diâmetro vale para o cilíndrico.
     */
    public record Resultado(
            FormaTanque forma,
            double profundidadeUtilM,
            double profundidadeMinimaM,
            double profundidadeMaximaM,
            Double larguraM,
            Double comprimentoM,
            Double relacaoComprimentoLargura,
            Double diametroM,
            double alturaConstrutivaM,
            double volumeRealM3) {
    }

    /**
     * @param volumeUtilM3          volume útil requerido, já com o mínimo normativo aplicado
     * @param forma                 forma construtiva desejada
     * @param profundidadeUtilM     profundidade útil escolhida pelo projetista, ou {@code null}
     *                              para adotar a menor profundidade da faixa que resolva
     */
    public static Resultado resolver(double volumeUtilM3, FormaTanque forma, Double profundidadeUtilM) {
        Faixa faixa = Faixa.para(volumeUtilM3);

        if (profundidadeUtilM != null) {
            int alturaCm = (int) Math.round(profundidadeUtilM * 100);

            if (alturaCm < faixa.minimoCm() || alturaCm > faixa.maximoCm()) {
                throw new RegraNegocioException(String.format(
                        "Para um volume útil de %.2f m³, a profundidade útil deve ficar entre %.2f m e %.2f m.",
                        volumeUtilM3, faixa.minimoM(), faixa.maximoM()));
            }

            return resolverPara(volumeUtilM3, forma, alturaCm, faixa)
                    .orElseThrow(() -> new RegraNegocioException(semSolucao(forma, faixa, true)));
        }

        for (int alturaCm = faixa.minimoCm(); alturaCm <= faixa.maximoCm(); alturaCm += PASSO_CM) {
            Optional<Resultado> resultado = resolverPara(volumeUtilM3, forma, alturaCm, faixa);
            if (resultado.isPresent()) return resultado.get();
        }

        throw new RegraNegocioException(semSolucao(forma, faixa, false));
    }

    private static Optional<Resultado> resolverPara(double volumeUtilM3, FormaTanque forma,
                                                    int alturaCm, Faixa faixa) {
        return forma == FormaTanque.CILINDRICO
                ? cilindrico(volumeUtilM3, alturaCm, faixa)
                : prismatico(volumeUtilM3, alturaCm, faixa);
    }

    /**
     * Percorre as larguras admissíveis e, para cada uma, adota o menor comprimento que cobre a
     * área requerida sem violar a relação comprimento/largura. Vence a combinação que sobra
     * menos — o excesso é volume escavado e concretado à toa.
     */
    private static Optional<Resultado> prismatico(double volumeUtilM3, int alturaCm, Faixa faixa) {
        double alturaM = alturaCm / 100.0;
        long areaRequeridaCm2 = areaRequeridaCm2(volumeUtilM3, alturaM);

        // Acima desta largura o comprimento mínimo (2 x largura) já cobre a área sozinho, e o
        // excesso só cresce; o passo extra cobre o arredondamento da raiz.
        int larguraMaximaCm = Math.max(
                LARGURA_MINIMA_CM,
                arredondarAcima((int) Math.ceil(Math.sqrt(areaRequeridaCm2 / (double) RELACAO_MINIMA))))
                + PASSO_CM;

        long menorExcesso = Long.MAX_VALUE;
        int larguraCm = 0;
        int comprimentoCm = 0;

        for (int largura = LARGURA_MINIMA_CM; largura <= larguraMaximaCm; largura += PASSO_CM) {
            int comprimento = Math.max(
                    RELACAO_MINIMA * largura,
                    arredondarAcima((int) Math.ceil(areaRequeridaCm2 / (double) largura)));

            if (comprimento > RELACAO_MAXIMA * largura) continue;

            long excesso = (long) largura * comprimento - areaRequeridaCm2;
            if (excesso < menorExcesso) {
                menorExcesso = excesso;
                larguraCm = largura;
                comprimentoCm = comprimento;
            }
        }

        if (larguraCm == 0) return Optional.empty();

        double largura = larguraCm / 100.0;
        double comprimento = comprimentoCm / 100.0;

        return Optional.of(new Resultado(
                FormaTanque.PRISMATICO_RETANGULAR,
                alturaM,
                faixa.minimoM(),
                faixa.maximoM(),
                largura,
                comprimento,
                arredondar(comprimento / largura, 2),
                null,
                alturaM + CAMARA_GASES_CM / 100.0,
                arredondar(largura * comprimento * alturaM, 3)));
    }

    private static Optional<Resultado> cilindrico(double volumeUtilM3, int alturaCm, Faixa faixa) {
        double alturaM = alturaCm / 100.0;
        double areaRequeridaM2 = volumeUtilM3 / alturaM;

        // Menor diâmetro múltiplo de 0,05 m cuja seção cobre a área requerida.
        int diametroCm = Math.max(
                DIAMETRO_MINIMO_CM,
                arredondarAcima((int) Math.ceil(Math.sqrt(4 * areaRequeridaM2 / Math.PI) * 100 - 1e-6)));

        if (diametroCm > RELACAO_MINIMA * alturaCm) return Optional.empty();

        double diametro = diametroCm / 100.0;
        double volumeReal = Math.PI * diametro * diametro / 4 * alturaM;

        return Optional.of(new Resultado(
                FormaTanque.CILINDRICO,
                alturaM,
                faixa.minimoM(),
                faixa.maximoM(),
                null,
                null,
                null,
                diametro,
                alturaM + CAMARA_GASES_CM / 100.0,
                arredondar(volumeReal, 3)));
    }

    private static long areaRequeridaCm2(double volumeUtilM3, double alturaM) {
        return (long) Math.ceil(volumeUtilM3 / alturaM * 10_000 - 1e-6);
    }

    /** Próximo múltiplo de 0,05 m, em centímetros. */
    private static int arredondarAcima(int centimetros) {
        return (centimetros + PASSO_CM - 1) / PASSO_CM * PASSO_CM;
    }

    private static double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }

    private static String semSolucao(FormaTanque forma, Faixa faixa, boolean alturaInformada) {
        String limites = String.format("entre %.2f m e %.2f m", faixa.minimoM(), faixa.maximoM());

        if (forma == FormaTanque.CILINDRICO) {
            return alturaInformada
                    ? "Nesta profundidade o tanque cilíndrico exigiria um diâmetro maior que o dobro da"
                            + " profundidade útil. Aumente a profundidade ou adote a forma prismática retangular."
                    : "Não há tanque cilíndrico possível para este volume com profundidade útil " + limites
                            + " — o diâmetro necessário passaria do dobro da profundidade."
                            + " Adote a forma prismática retangular.";
        }

        return "Não foi possível fechar as dimensões internas respeitando largura mínima de 0,80 m e"
                + " relação comprimento/largura entre 2,0 e 4,0 com profundidade útil " + limites + ".";
    }
}
