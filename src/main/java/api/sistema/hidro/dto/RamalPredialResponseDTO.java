package api.sistema.hidro.dto;

import api.sistema.hidro.enums.HidrometroPadrao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RamalPredialResponseDTO {
    private Long id;
    private Long empreendimentoId;

    private Integer taxaOcupacao;
    private Integer numUnidades;
    private Integer consumoPerCapita;
    private Integer tempoReposicaoH;
    private Integer tempoReposicaoMaximoH;
    private Double velocidadeMaximaMs;

    private String formula;

    private Integer populacao;
    private Double consumoDiarioM3;
    private Double consumoMensalM3;
    private Double vazaoProjetoM3h;
    private Double vazaoProjetoLs;

    private Double diametroTeoricoMm;
    private Integer dnAdotadoMm;
    private Double diametroInternoMm;
    private Double velocidadeMs;

    private HidrometroPadrao hidrometro;
    private Double hidrometroVazaoNominalM3h;
    private Double hidrometroVazaoMaximaM3h;
    private HidrometroPadrao hidrometroInformado;
    private Boolean hidrometroFixadoManualmente;
    private String concessionaria;

    private List<String> alertas;

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
