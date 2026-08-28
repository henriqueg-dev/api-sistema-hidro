package api.sistema.hidro.dto;

import api.sistema.hidro.enums.SentidoTrecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TrechoPiscinaResponseDTO {
    private Long id;
    private Integer ordem;
    private String nome;
    private SentidoTrecho sentido;
    private Double vazaoM3h;
    private Double vazaoLs;
    private Integer dnMm;
    private Double diametroInternoMm;
    private Double velocidadeMs;
    private Double perdaUnitariaMM;
    private Double desnivelM;
    private Double lEquivalenteAdicionalM;
    private Double lEquivalenteM;
    private Double lRealM;
    private Double lTotalM;
    private Double hfM;
    private Double pressaoMontanteMca;
    private Double pressaoJusanteMca;
    private List<ConexaoTrechoResponseDTO> conexoes;
}
