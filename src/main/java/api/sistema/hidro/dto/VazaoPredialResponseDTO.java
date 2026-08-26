package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class VazaoPredialResponseDTO {
    private Long id;
    private Long empreendimentoId;

    private Integer taxaOcupacao;
    private Integer numApartamentos;
    private Integer consumoPerCapita;
    private Integer capacidadeEquivalenteDias;

    private Double k1;
    private Double k2;
    private Double fracaoInferior;
    private Double fracaoSuperior;

    private Integer populacao;
    private Double volumeCaixaM3;
    private Double volumeInferiorM3;
    private Double volumeSuperiorM3;
    private Double vazaoMediaLps;
    private Double vazaoMaximaDiariaLps;
    private Double vazaoMaximaHoraLps;
    private Double vazaoMaximaHoraM3h;
    private Double vazaoMaximaHoraM3mes;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
