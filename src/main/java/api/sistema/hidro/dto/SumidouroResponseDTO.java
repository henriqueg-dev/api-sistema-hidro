package api.sistema.hidro.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class SumidouroResponseDTO {
    private Long id;
    private Long empreendimentoId;

    private Integer contribuicaoDiariaLitros;
    private Double taxaPercolacao;
    private Double diametro;
    private Integer numSumidouros;

    private String formula;
    private Double taxaAplicacao;
    private Double contribuicaoM3Dia;
    private Double areaTotalM2;
    private Double areaPorSumidouroM2;
    private Double areaFundoM2;
    private Double alturaTeoricaM;
    private Double alturaUtilM;
    private Double areaRealM2;
    private Double volumeUtilM3;

    private List<String> alertas;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
