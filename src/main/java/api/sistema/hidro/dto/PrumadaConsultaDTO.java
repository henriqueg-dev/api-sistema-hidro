package api.sistema.hidro.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrumadaConsultaDTO {
    private String tipo;
    private Integer numPavimentos;
    private String desconector;
    private String condicaoSanca;
}