package api.sistema.hidro.dto;

import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.FaixaTemperatura;
import api.sistema.hidro.enums.FormaTanque;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TanqueSepticoRequestDTO {

    @NotNull(message = "Empreendimento é obrigatório")
    private Long empreendimentoId;

    @NotNull(message = "Taxa de ocupação é obrigatória")
    @Positive(message = "Taxa de ocupação deve ser maior que zero")
    private Integer taxaOcupacao;

    @NotNull(message = "Número de unidades é obrigatório")
    @Positive(message = "Número de unidades deve ser maior que zero")
    private Integer numUnidades;

    @NotNull(message = "Tipo de contribuição é obrigatório")
    private ContribuicaoDespejo contribuicaoDespejo;

    @NotNull(message = "Faixa de temperatura é obrigatória")
    private FaixaTemperatura faixaTemperatura;

    // A Tabela 3 da NBR 7229 só tabela K para intervalos de 1 a 5 anos.
    @NotNull(message = "Intervalo de limpeza é obrigatório")
    @Min(value = 1, message = "Intervalo de limpeza deve ser de no mínimo 1 ano")
    @Max(value = 5, message = "Intervalo de limpeza deve ser de no máximo 5 anos")
    private Integer intervaloLimpezaAnos;

    /** Opcional: sem valor, adota-se o tanque prismático retangular. */
    private FormaTanque formaTanque;

    /**
     * Opcional: sem valor, adota-se a menor profundidade útil da faixa normativa que resolva a
     * geometria. A faixa depende do volume, por isso a validação dos limites fica no cálculo.
     */
    @Positive(message = "Profundidade útil deve ser maior que zero")
    private Double profundidadeUtilM;
}
