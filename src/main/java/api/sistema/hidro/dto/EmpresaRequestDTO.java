package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;
}