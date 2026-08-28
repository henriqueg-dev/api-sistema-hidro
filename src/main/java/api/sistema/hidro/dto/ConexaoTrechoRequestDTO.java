package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoConexao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConexaoTrechoRequestDTO {

    @NotNull(message = "Tipo da conexão é obrigatório")
    private TipoConexao tipo;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantidade;
}
