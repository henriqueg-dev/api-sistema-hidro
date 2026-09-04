package api.sistema.hidro.entity;

import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.FaixaTemperatura;
import api.sistema.hidro.enums.FormaTanque;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "tb_tanque_septico")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TanqueSepticoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empreendimento_id", nullable = false)
    private EmpreendimentoEntity empreendimento;

    @Column(name = "taxa_ocupacao", nullable = false)
    private Integer taxaOcupacao;

    @Column(name = "num_unidades", nullable = false)
    private Integer numUnidades;

    @Enumerated(EnumType.STRING)
    @Column(name = "contribuicao_despejo", nullable = false)
    private ContribuicaoDespejo contribuicaoDespejo;

    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_temperatura", nullable = false)
    private FaixaTemperatura faixaTemperatura;

    @Column(name = "intervalo_limpeza_anos", nullable = false)
    private Integer intervaloLimpezaAnos;

    @Column(nullable = false)
    private Integer populacao;

    @Column(name = "contribuicao_diaria_litros", nullable = false)
    private Integer contribuicaoDiariaLitros;

    @Column(name = "periodo_detencao_dias", nullable = false)
    private Double periodoDetencaoDias;

    @Column(name = "taxa_acumulacao_dias", nullable = false)
    private Integer taxaAcumulacaoDias;

    @Column(name = "volume_calculado_litros", nullable = false)
    private Integer volumeCalculadoLitros;

    @Column(name = "volume_litros", nullable = false)
    private Integer volumeLitros;

    // Geometria resolvida a partir do volume útil. As colunas são anuláveis porque um cálculo
    // gravado antes desta etapa existir não tem dimensões — a tela trata essa ausência.
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_tanque")
    private FormaTanque formaTanque;

    /** Profundidade útil escolhida pelo projetista; nula quando adotado o padrão da faixa. */
    @Column(name = "profundidade_informada_m")
    private Double profundidadeInformadaM;

    @Column(name = "profundidade_util_m")
    private Double profundidadeUtilM;

    @Column(name = "largura_m")
    private Double larguraM;

    @Column(name = "comprimento_m")
    private Double comprimentoM;

    @Column(name = "relacao_comprimento_largura")
    private Double relacaoComprimentoLargura;

    @Column(name = "diametro_m")
    private Double diametroM;

    @Column(name = "altura_construtiva_m")
    private Double alturaConstrutivaM;

    @Column(name = "volume_real_m3")
    private Double volumeRealM3;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
