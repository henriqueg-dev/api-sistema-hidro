package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoUsoPiscina;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PiscinaResponseDTO {
    private Long id;
    private Long empreendimentoId;
    private String nome;
    private TipoUsoPiscina tipoUso;

    private Double larguraM;
    private Double comprimentoM;
    private Double profundidadeM;
    private Integer tempoFiltracaoH;
    private Integer tempoMaximoFiltracaoH;
    private Double vazaoBombaM3h;
    private Double alturaManometricaMca;
    private Integer areaPorSkimmerM2;

    private Double areaM2;
    private Double volumeM3;
    private Double vazaoProjetoM3h;

    private Integer dnRecalqueMm;
    private Integer dnSuccaoMm;
    private Double velocidadeRecalqueMs;
    private Double velocidadeSuccaoMs;

    /** Maior entre os dois critérios seguintes: Qb/5 e A/50. */
    private Double numBocaisRetornoCalculado;
    private Double numBocaisPorVazao;
    private Double numBocaisPorArea;
    private Integer numBocaisRetornoAdotado;
    private Double numSkimmersCalculado;
    private Integer numSkimmersAdotado;
    private Double numRalosCalculado;
    private Integer numRalosAdotado;
    private Integer numAspiradores;

    private Double pressaoResidualMca;

    private List<TrechoPiscinaResponseDTO> trechos;

    /** Avisos de conformidade que não impedem o cálculo. */
    private List<String> alertas;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
