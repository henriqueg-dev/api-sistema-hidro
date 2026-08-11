package api.sistema.hidro.dto;

import api.sistema.hidro.enums.CondicaoSanca;
import api.sistema.hidro.enums.TipoPrumada;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrumadaConsultaDTO {

    @NotNull(message = "Tipo é obrigatório")
    private TipoPrumada tipo;

    @NotNull(message = "Número de pavimentos é obrigatório")
    @Positive(message = "Número de pavimentos deve ser maior que zero")
    private Integer numPavimentos;

    @NotBlank(message = "Desconector é obrigatório")
    private String desconector;

    @NotNull(message = "Condição da sanca é obrigatória")
    private CondicaoSanca condicaoSanca;
}
