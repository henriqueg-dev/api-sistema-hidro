package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SumidouroRequestDTO {

    @NotNull(message = "Empreendimento é obrigatório")
    private Long empreendimentoId;

    @NotNull(message = "Contribuição diária é obrigatória")
    @Positive(message = "Contribuição diária deve ser maior que zero")
    private Integer contribuicaoDiariaLitros;

    @NotNull(message = "Taxa de percolação é obrigatória")
    @Positive(message = "Taxa de percolação deve ser maior que zero")
    private Double taxaPercolacao;

    @NotNull(message = "Diâmetro é obrigatório")
    @Positive(message = "Diâmetro deve ser maior que zero")
    private Double diametro;

    @NotNull(message = "Número de sumidouros é obrigatório")
    @Positive(message = "Número de sumidouros deve ser maior que zero")
    private Integer numSumidouros;
}
