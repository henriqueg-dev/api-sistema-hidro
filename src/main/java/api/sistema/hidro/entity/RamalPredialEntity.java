package api.sistema.hidro.entity;

import api.sistema.hidro.enums.HidrometroPadrao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "tb_ramal_predial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RamalPredialEntity {

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

    @Column(name = "consumo_per_capita", nullable = false)
    private Integer consumoPerCapita;

    @Column(name = "tempo_reposicao_h", nullable = false)
    private Integer tempoReposicaoH;

    @Column(name = "velocidade_maxima_ms", nullable = false)
    private Double velocidadeMaximaMs;

    @Column(nullable = false)
    private Integer populacao;

    @Column(name = "consumo_diario_m3", nullable = false)
    private Double consumoDiarioM3;

    @Column(name = "consumo_mensal_m3", nullable = false)
    private Double consumoMensalM3;

    @Column(name = "vazao_projeto_m3h", nullable = false)
    private Double vazaoProjetoM3h;

    @Column(name = "vazao_projeto_ls", nullable = false)
    private Double vazaoProjetoLs;

    @Column(name = "diametro_teorico_mm", nullable = false)
    private Double diametroTeoricoMm;

    @Column(name = "dn_adotado_mm", nullable = false)
    private Integer dnAdotadoMm;

    @Column(name = "diametro_interno_mm", nullable = false)
    private Double diametroInternoMm;

    @Column(name = "velocidade_ms", nullable = false)
    private Double velocidadeMs;

    @Enumerated(EnumType.STRING)
    @Column(name = "hidrometro", nullable = false)
    private HidrometroPadrao hidrometro;

    /** Preenchido quando o projetista fixa o medidor no lugar da sugestão. */
    @Enumerated(EnumType.STRING)
    @Column(name = "hidrometro_informado")
    private HidrometroPadrao hidrometroInformado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
