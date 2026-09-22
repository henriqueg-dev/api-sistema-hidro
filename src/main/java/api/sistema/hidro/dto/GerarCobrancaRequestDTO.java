package api.sistema.hidro.dto;

import api.sistema.hidro.enums.PlanoAssinatura;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GerarCobrancaRequestDTO {

    @NotNull(message = "Plano é obrigatório")
    private PlanoAssinatura plano;
}
