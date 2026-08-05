package api.sistema.hidro.dto;

import api.sistema.hidro.enums.CondicaoSanca;
import api.sistema.hidro.enums.TipoPrumada;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrumadaRespostaDTO {
    private TipoPrumada tipo;
    private String numPavimentos;
    private String desconector;
    private CondicaoSanca condicaoSanca;
    private String descricao;
}
