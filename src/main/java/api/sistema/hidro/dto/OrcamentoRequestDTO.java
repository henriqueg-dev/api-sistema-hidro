package api.sistema.hidro.dto;

import api.sistema.hidro.enums.StatusOrcamento;
import api.sistema.hidro.enums.TipoEmpreendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrcamentoRequestDTO {

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;

    @NotBlank(message = "Nome do empreendimento é obrigatório")
    private String nomeEmpreendimento;

    @NotNull(message = "Tipo do empreendimento é obrigatório")
    private TipoEmpreendimento tipoEmpreendimento;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Double quantidade;

    @NotNull(message = "Valor unitário é obrigatório")
    @Positive(message = "Valor unitário deve ser maior que zero")
    private Double valorUnitario;

    @NotNull(message = "Status é obrigatório")
    private StatusOrcamento status;

    private String observacoes;

    @Positive(message = "Validade deve ser maior que zero")
    private Integer validadeDias;

    /** Só usados quando o status vira APROVADO e o orçamento ainda não virou empreendimento. */
    @Positive(message = "Número de pavimentos deve ser maior que zero")
    private Integer numPavimentos;

    private String endereco;

    private String concessionaria;
}
