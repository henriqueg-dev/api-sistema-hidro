package api.sistema.hidro.dto;

import api.sistema.hidro.enums.HidrometroPadrao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RamalPredialRequestDTO {

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

    /** Nulo adota o máximo da norma para o tipo de empreendimento. */
    @Min(value = 1, message = "Tempo de reposição deve ser de no mínimo 1 hora")
    private Integer tempoReposicaoH;

    @Positive(message = "Velocidade máxima deve ser maior que zero")
    private Double velocidadeMaximaMs;

    /** Nulo adota o menor medidor que cobre a vazão de projeto. */
    private HidrometroPadrao hidrometroInformado;
}
