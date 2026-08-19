package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaixaGorduraRequestDTO {

    @NotNull(message = "Empreendimento é obrigatório")
    private Long empreendimentoId;

    @NotNull(message = "Taxa de ocupação é obrigatória")
    @Positive(message = "Taxa de ocupação deve ser maior que zero")
    private Integer taxaOcupacao;

    @NotNull(message = "Número de apartamentos é obrigatório")
    @Positive(message = "Número de apartamentos deve ser maior que zero")
    private Integer numApartamentos;
}
