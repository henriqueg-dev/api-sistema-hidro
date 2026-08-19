package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CaixaGorduraResponseDTO {
    private Long id;
    private Long empreendimentoId;
    private Integer taxaOcupacao;
    private Integer numApartamentos;
    private Integer populacao;
    private String formula;
    private Integer volumeLitros;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
