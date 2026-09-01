package api.sistema.hidro.service;

import api.sistema.hidro.enums.DiametroRamal;
import api.sistema.hidro.enums.HidrometroPadrao;
import api.sistema.hidro.enums.TipoEmpreendimento;

/**
 * Alimentador predial e hidrômetro, pela ABNT NBR 5626:2020.
 *
 * <p>A norma manda repor o volume de consumo diário em até 6 h, ou 3 h em residência
 * unifamiliar (6.7). O limite de 3 m/s aparece como nota do item 6.8.3: não evita golpe de
 * aríete, apenas limita a sobrepressão.
 */
public final class CalculoRamalPredial {

    public static final int TEMPO_REPOSICAO_MAXIMO_H = 6;
    public static final int TEMPO_REPOSICAO_UNIFAMILIAR_H = 3;
    public static final double VELOCIDADE_MAXIMA_PADRAO_MS = 3.0;

    private static final double M3H_PARA_LS = 3.6;
    private static final int DIAS_NO_MES = 30;
    private static final int LITROS_POR_M3 = 1000;

    private CalculoRamalPredial() {
    }

    public record Resultado(
            int populacao,
            double consumoDiarioM3,
            double consumoMensalM3,
            int tempoReposicaoH,
            int tempoReposicaoMaximoH,
            double vazaoProjetoM3h,
            double vazaoProjetoLs,
            double diametroTeoricoMm,
            DiametroRamal diametroAdotado,
            double velocidadeMs,
            double velocidadeMaximaMs,
            HidrometroPadrao hidrometro) {
    }

    /** Tempo de reposição máximo admitido para o tipo de empreendimento, em horas. */
    public static int tempoReposicaoMaximo(TipoEmpreendimento tipo) {
        return tipo == TipoEmpreendimento.CASA
                ? TEMPO_REPOSICAO_UNIFAMILIAR_H
                : TEMPO_REPOSICAO_MAXIMO_H;
    }

    public static Resultado dimensionar(TipoEmpreendimento tipo, int taxaOcupacao, int numUnidades,
                                        int consumoPerCapitaLitros, Integer tempoReposicaoInformadoH,
                                        Double velocidadeMaximaInformadaMs,
                                        HidrometroPadrao hidrometroInformado) {
        int populacao = taxaOcupacao * numUnidades;
        double consumoDiarioM3 = (double) populacao * consumoPerCapitaLitros / LITROS_POR_M3;

        int tempoMaximo = tempoReposicaoMaximo(tipo);
        int tempoReposicao = tempoReposicaoInformadoH != null ? tempoReposicaoInformadoH : tempoMaximo;

        if (tempoReposicao > tempoMaximo) {
            throw new IllegalArgumentException(String.format(
                    "tempo de reposição de %d h acima do máximo de %d h", tempoReposicao, tempoMaximo));
        }

        double velocidadeMaxima = velocidadeMaximaInformadaMs != null
                ? velocidadeMaximaInformadaMs
                : VELOCIDADE_MAXIMA_PADRAO_MS;

        double vazaoM3h = consumoDiarioM3 / tempoReposicao;
        double vazaoLs = vazaoM3h / M3H_PARA_LS;

        // D = raiz(4Q / pi.v), com Q em m3/s, devolvido em mm
        double diametroTeoricoMm =
                Math.sqrt(4 * (vazaoLs / 1000.0) / (Math.PI * velocidadeMaxima)) * 1000;

        DiametroRamal adotado = DiametroRamal.menorPara(vazaoLs, velocidadeMaxima);

        HidrometroPadrao hidrometro = hidrometroInformado != null
                ? hidrometroInformado
                : HidrometroPadrao.menorPara(vazaoM3h);

        return new Resultado(
                populacao,
                arredondar(consumoDiarioM3, 3),
                arredondar(consumoDiarioM3 * DIAS_NO_MES, 2),
                tempoReposicao,
                tempoMaximo,
                arredondar(vazaoM3h, 3),
                arredondar(vazaoLs, 4),
                arredondar(diametroTeoricoMm, 2),
                adotado,
                arredondar(adotado.velocidadeMs(vazaoLs), 3),
                velocidadeMaxima,
                hidrometro);
    }

    private static double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }
}
