package api.sistema.hidro.dto;

import api.sistema.hidro.enums.PlanoAssinatura;
import api.sistema.hidro.enums.StatusAssinatura;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AssinaturaResponseDTO {
    private PlanoAssinatura plano;
    private StatusAssinatura status;
    private LocalDateTime expiraEm;
}
