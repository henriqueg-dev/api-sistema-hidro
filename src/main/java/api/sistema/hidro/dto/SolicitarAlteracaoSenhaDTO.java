package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitarAlteracaoSenhaDTO {

    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;
}
