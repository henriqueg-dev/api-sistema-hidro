package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpreendimentoRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Tipo é obrigatório")
    private String tipo;

    @NotNull(message = "Número de pavimentos é obrigatório")
    private Integer numPavimentos;

    @NotBlank(message = "Endereço é obrigatório")
    private String endereco;

    @NotBlank(message = "Concessionária é obrigatória")
    private String concessionaria;

    @NotNull(message = "Empresa é obrigatória")
    private Long empresaId;
}