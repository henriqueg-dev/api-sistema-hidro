package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/** Tabelas de apoio da NBR 10339 e de catálogo, para a tela montar selects e consultas. */
@Getter
@Setter
@AllArgsConstructor
public class PiscinaReferenciasDTO {

    /** Tipo de uso -> tempo máximo de filtração por faixa de profundidade. */
    private List<Map<String, Object>> tempoFiltracao;

    /** DN -> diâmetro interno do tubo de PVC classe 15. */
    private List<Map<String, Object>> diametros;

    /** Faixas de vazão que definem o DN de recalque e de sucção. */
    private List<Map<String, Object>> faixasRecalque;
    private List<Map<String, Object>> faixasSuccao;

    /** Comprimento equivalente por conexão e DN. */
    private List<Integer> diametrosTabelados;
    private List<Map<String, Object>> conexoes;

    private Double velocidadeMaximaSuccaoMs;
    private Double velocidadeMaximaRecalqueMs;
}
