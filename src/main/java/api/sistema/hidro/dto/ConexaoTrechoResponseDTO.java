package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoConexao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConexaoTrechoResponseDTO {
    private Long id;
    private TipoConexao tipo;
    private Integer quantidade;
    private Double comprimentoEquivalenteM;
}
