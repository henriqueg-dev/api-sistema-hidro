package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class AlteracaoDTO {
    private String tipo;
    private String nome;
    private String acao;
    private List<String> campos;
}
