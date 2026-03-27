package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrumadaRespostaDTO {
    private String tipo;
    private String numPavimentos;
    private String desconector;
    private String condicaoSanca;
    private String descricao;
}