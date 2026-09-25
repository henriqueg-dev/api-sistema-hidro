package api.sistema.hidro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

/** Dados de entrada do sumidouro; o dimensionamento é refeito a partir deles na leitura. */
@Audited
@Entity
@Table(name = "tb_sumidouro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SumidouroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empreendimento_id", nullable = false)
    private EmpreendimentoEntity empreendimento;

    @Column(name = "contribuicao_diaria_litros", nullable = false)
    private Integer contribuicaoDiariaLitros;

    /** Minutos por metro, medida no ensaio de percolação. */
    @Column(name = "taxa_percolacao", nullable = false)
    private Double taxaPercolacao;

    /** Diâmetro interno, em metros. */
    @Column(nullable = false)
    private Double diametro;

    @Column(name = "num_sumidouros", nullable = false)
    private Integer numSumidouros;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
