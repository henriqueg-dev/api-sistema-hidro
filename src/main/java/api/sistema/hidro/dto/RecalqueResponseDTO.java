package api.sistema.hidro.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class RecalqueResponseDTO {
    private Long id;
    private Long empreendimentoId;

    private Integer taxaOcupacao;
    private Integer numUnidades;
    private Integer consumoPerCapita;
    private Double horasFuncionamento;
    private Double desnivelSuccao;
    private Double comprimentoSuccao;
    private List<ConexaoTrechoResponseDTO> conexoesSuccao;
    private Double desnivelRecalque;
    private Double comprimentoRecalque;
    private List<ConexaoTrechoResponseDTO> conexoesRecalque;
    private Integer rendimentoPercentual;

    private Integer populacao;
    private Double consumoDiarioLitros;
    private Double vazaoLs;
    private Double vazaoM3h;
    /** Horas acima das quais a vazão fica abaixo de 15% do consumo diário por hora. */
    private Double horasMaximas;
    private Double diametroTeoricoMm;

    private Integer dnRecalqueMm;
    private Double internoRecalqueMm;
    private Double velocidadeRecalqueMs;
    private Double perdaUnitariaRecalque;
    private Double comprimentoEquivalenteRecalqueM;
    private Double perdaCargaRecalqueM;

    private Integer dnSuccaoMm;
    private Double internoSuccaoMm;
    private Double velocidadeSuccaoMs;
    private Double perdaUnitariaSuccao;
    private Double comprimentoEquivalenteSuccaoM;
    private Double perdaCargaSuccaoM;

    private Double cargaVelocidadeM;
    private Double alturaManometricaM;
    private Double potenciaCv;
    private Double potenciaKw;
    private Integer folgaPercentual;
    private Double potenciaComFolgaCv;
    /** Nulo quando a potência passa do maior motor da lista. */
    private Double motorComercialCv;

    private List<String> alertas;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
