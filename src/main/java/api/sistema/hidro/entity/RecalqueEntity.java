package api.sistema.hidro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

/** Dados de entrada do recalque; o dimensionamento é refeito a partir deles na leitura. */
@Audited
@Entity
@Table(name = "tb_recalque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecalqueEntity {

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

    @Column(name = "horas_funcionamento", nullable = false)
    private Double horasFuncionamento;

    /** Metros; negativo quando a bomba está abaixo do nível d'água (sucção afogada). */
    @Column(name = "desnivel_succao", nullable = false)
    private Double desnivelSuccao;

    @Column(name = "comprimento_succao", nullable = false)
    private Double comprimentoSuccao;

    @Column(name = "desnivel_recalque", nullable = false)
    private Double desnivelRecalque;

    @Column(name = "comprimento_recalque", nullable = false)
    private Double comprimentoRecalque;

    @Column(name = "rendimento_percentual", nullable = false)
    private Integer rendimentoPercentual;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
