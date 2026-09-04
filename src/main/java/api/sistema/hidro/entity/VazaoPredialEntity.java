package api.sistema.hidro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "tb_vazao_predial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VazaoPredialEntity {

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

    @Column(name = "consumo_per_capita", nullable = false)
    private Integer consumoPerCapita;

    @Column(name = "capacidade_equivalente_dias", nullable = false)
    private Integer capacidadeEquivalenteDias;

    @Column(nullable = false)
    private Integer populacao;

    @Column(name = "volume_caixa_m3", nullable = false)
    private Double volumeCaixaM3;

    @Column(name = "vazao_media_lps", nullable = false)
    private Double vazaoMediaLps;

    @Column(name = "vazao_maxima_diaria_lps", nullable = false)
    private Double vazaoMaximaDiariaLps;

    @Column(name = "vazao_maxima_hora_lps", nullable = false)
    private Double vazaoMaximaHoraLps;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
