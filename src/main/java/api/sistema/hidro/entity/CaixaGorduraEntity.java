package api.sistema.hidro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Cálculo de caixa de gordura e sabão. Cada empreendimento pode ter no máximo
 * dois — o limite é aplicado em {@link api.sistema.hidro.service.CaixaGorduraService}.
 */
@Entity
@Table(name = "tb_caixa_gordura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaixaGorduraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empreendimento_id", nullable = false)
    private EmpreendimentoEntity empreendimento;

    @Column(name = "taxa_ocupacao", nullable = false)
    private Integer taxaOcupacao;

    @Column(name = "num_apartamentos", nullable = false)
    private Integer numApartamentos;

    @Column(nullable = false)
    private Integer populacao;

    @Column(name = "volume_litros", nullable = false)
    private Integer volumeLitros;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
