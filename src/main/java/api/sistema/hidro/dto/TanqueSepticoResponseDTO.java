package api.sistema.hidro.dto;

import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.FaixaTemperatura;
import api.sistema.hidro.enums.FormaTanque;
import api.sistema.hidro.enums.UnidadeContribuicao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TanqueSepticoResponseDTO {
    private Long id;
    private Long empreendimentoId;

    private Integer taxaOcupacao;
    private Integer numUnidades;
    private ContribuicaoDespejo contribuicaoDespejo;
    private FaixaTemperatura faixaTemperatura;
    private Integer intervaloLimpezaAnos;

    private String formula;

    /** N — número de pessoas ou unidades de contribuição. */
    private Integer populacao;
    /** O que N conta neste tipo de prédio: pessoa, refeição, lugar ou bacia sanitária. */
    private UnidadeContribuicao unidadeContribuicao;
    /** C — contribuição de despejos, em L/pessoa.dia. */
    private Integer contribuicaoLitros;
    /** Lf — contribuição de lodo fresco, em L/pessoa.dia. */
    private Double lodoFrescoLitros;
    /** T — período de detenção, em dias. */
    private Double periodoDetencaoDias;
    /** K — taxa de acumulação total de lodo, em dias. */
    private Integer taxaAcumulacaoDias;

    private Integer contribuicaoDiariaLitros;
    private Integer vazaoMaximaLitrosDia;
    private Boolean vazaoAcimaDoMetodo;
    private Integer volumeCalculadoLitros;
    private Integer volumeLitros;
    private Integer volumeMinimoLitros;
    private Boolean volumeMinimoAplicado;

    // Geometria adotada para o volume útil acima.
    private FormaTanque formaTanque;
    /** Profundidade escolhida pelo projetista; nula quando adotado o padrão da faixa. */
    private Double profundidadeUtilM;
    /** Profundidade efetivamente adotada no dimensionamento. */
    private Double profundidadeAdotadaM;
    private Double profundidadeMinimaM;
    private Double profundidadeMaximaM;
    private Double larguraM;
    private Double comprimentoM;
    private Double relacaoComprimentoLargura;
    private Double diametroM;
    private Double alturaConstrutivaM;
    private Double camaraGasesM;
    private Double volumeRealM3;
    private Double folgaM3;
    private Double folgaPercentual;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
