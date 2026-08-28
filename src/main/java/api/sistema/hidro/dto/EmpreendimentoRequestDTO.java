package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoEmpreendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpreendimentoRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Tipo é obrigatório")
    private TipoEmpreendimento tipo;

    @NotNull(message = "Número de pavimentos é obrigatório")
    @Positive(message = "Número de pavimentos deve ser maior que zero")
    private Integer numPavimentos;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Concessionária é obrigatória")
    private String concessionaria;

    @NotNull(message = "Cliente é obrigatório")
    private Long clienteId;
}
