package api.sistema.hidro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecalqueRequestDTO {

    @NotNull(message = "Empreendimento é obrigatório")
    private Long empreendimentoId;

    @NotNull(message = "Taxa de ocupação é obrigatória")
    @Positive(message = "Taxa de ocupação deve ser maior que zero")
    private Integer taxaOcupacao;

    @NotNull(message = "Número de unidades é obrigatório")
    @Positive(message = "Número de unidades deve ser maior que zero")
    private Integer numUnidades;

    @NotNull(message = "Consumo per capita é obrigatório")
    @Positive(message = "Consumo per capita deve ser maior que zero")
    private Integer consumoPerCapita;

    @NotNull(message = "Horas de funcionamento são obrigatórias")
    @Positive(message = "Horas de funcionamento devem ser maiores que zero")
    @DecimalMax(value = "24", message = "O dia tem no máximo 24 horas de funcionamento")
    private Double horasFuncionamento;

    @NotNull(message = "Desnível da sucção é obrigatório")
    private Double desnivelSuccao;

    @NotNull(message = "Comprimento da sucção é obrigatório")
    @PositiveOrZero(message = "Comprimento da sucção não pode ser negativo")
    private Double comprimentoSuccao;

    @Valid
    private List<ConexaoTrechoRequestDTO> conexoesSuccao = new ArrayList<>();

    @NotNull(message = "Desnível do recalque é obrigatório")
    private Double desnivelRecalque;

    @NotNull(message = "Comprimento do recalque é obrigatório")
    @PositiveOrZero(message = "Comprimento do recalque não pode ser negativo")
    private Double comprimentoRecalque;

    @Valid
    private List<ConexaoTrechoRequestDTO> conexoesRecalque = new ArrayList<>();

    @NotNull(message = "Rendimento da bomba é obrigatório")
    @Min(value = 1, message = "Rendimento deve ser de no mínimo 1%")
    @Max(value = 100, message = "Rendimento deve ser de no máximo 100%")
    private Integer rendimentoPercentual;
}
