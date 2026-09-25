package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/** Tabela de consulta já formatada para exibição: cabeçalho e linhas em texto. */
@Getter
@AllArgsConstructor
public class TabelaNormativaDTO {
    private String grupo;
    private String titulo;
    /** Norma e tabela de origem, ou a indicação de que o valor é adotado pelo sistema. */
    private String fonte;
    /** Cálculos do sistema que usam esta tabela. */
    private List<String> usadaEm;
    private List<String> colunas;
    private List<List<String>> linhas;
    private String nota;
}
