package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoUsoPiscina;
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
public class PiscinaRequestDTO {

    @NotNull(message = "Empreendimento é obrigatório")
    private Long empreendimentoId;

    @NotBlank(message = "Nome da piscina é obrigatório")
    private String nome;

    @NotNull(message = "Tipo de uso é obrigatório")
    private TipoUsoPiscina tipoUso;

    @NotNull(message = "Largura é obrigatória")
    @Positive(message = "Largura deve ser maior que zero")
    private Double larguraM;

    @NotNull(message = "Comprimento é obrigatório")
    @Positive(message = "Comprimento deve ser maior que zero")
    private Double comprimentoM;

    @NotNull(message = "Profundidade é obrigatória")
    @Positive(message = "Profundidade deve ser maior que zero")
    private Double profundidadeM;

    @NotNull(message = "Tempo de filtração é obrigatório")
    @Positive(message = "Tempo de filtração deve ser maior que zero")
    private Integer tempoFiltracaoH;

    @NotNull(message = "Vazão da bomba é obrigatória")
    @Positive(message = "Vazão da bomba deve ser maior que zero")
    private Double vazaoBombaM3h;

    @NotNull(message = "Altura manométrica é obrigatória")
    @Positive(message = "Altura manométrica deve ser maior que zero")
    private Double alturaManometricaMca;

    /** Nulo adota 50 m² por skimmer, o valor de piscina residencial. */
    @Positive(message = "Área por skimmer deve ser maior que zero")
    private Integer areaPorSkimmerM2;

    // Overrides do projetista: nulo mantém o valor calculado.

    @PositiveOrZero(message = "Número de bocais não pode ser negativo")
    private Integer numBocaisRetornoAdotado;

    @PositiveOrZero(message = "Número de skimmers não pode ser negativo")
    private Integer numSkimmersAdotado;

    @PositiveOrZero(message = "Número de ralos não pode ser negativo")
    private Integer numRalosAdotado;

    @NotNull(message = "Número de aspiradores é obrigatório")
    @PositiveOrZero(message = "Número de aspiradores não pode ser negativo")
    private Integer numAspiradores;

    @Valid
    private List<TrechoPiscinaRequestDTO> trechos = new ArrayList<>();
}
