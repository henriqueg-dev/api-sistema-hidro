package api.sistema.hidro.dto;

import api.sistema.hidro.enums.SentidoTrecho;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TrechoPiscinaRequestDTO {

    @NotBlank(message = "Nome do trecho é obrigatório")
    private String nome;

    @NotNull(message = "Sentido do trecho é obrigatório")
    private SentidoTrecho sentido;

    @NotNull(message = "Vazão do trecho é obrigatória")
    @Positive(message = "Vazão deve ser maior que zero")
    private Double vazaoM3h;

    @NotNull(message = "Diâmetro do trecho é obrigatório")
    @Positive(message = "Diâmetro deve ser maior que zero")
    private Integer dnMm;

    /** Pode ser negativo: o trecho pode descer. */
    @NotNull(message = "Desnível é obrigatório")
    private Double desnivelM;

    @NotNull(message = "Comprimento real é obrigatório")
    @PositiveOrZero(message = "Comprimento real não pode ser negativo")
    private Double lRealM;

    @PositiveOrZero(message = "Comprimento equivalente adicional não pode ser negativo")
    private Double lEquivalenteAdicionalM;

    @Valid
    private List<ConexaoTrechoRequestDTO> conexoes = new ArrayList<>();
}
