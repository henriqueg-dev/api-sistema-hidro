package api.sistema.hidro.entity;

import api.sistema.hidro.enums.SentidoTrecho;
import jakarta.persistence.*;
import lombok.*;

/** Trecho da tubulação de recirculação, do ralo até o bocal mais desfavorável. */
@Entity
@Table(name = "tb_trecho_piscina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrechoPiscinaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "piscina_id", nullable = false)
    private PiscinaEntity piscina;

    /** Define a sequência do balanço de pressão: cada trecho parte do anterior. */
    @Column(nullable = false)
    private Integer ordem;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SentidoTrecho sentido;

    // --- Entradas ---

    @Column(name = "vazao_m3h", nullable = false)
    private Double vazaoM3h;

    @Column(name = "dn_mm", nullable = false)
    private Integer dnMm;

    /** Positivo sobe, negativo desce. O sinal aplicado depende do sentido. */
    @Column(name = "desnivel_m", nullable = false)
    private Double desnivelM;

    @Column(name = "l_real_m", nullable = false)
    private Double lRealM;

    /** Comprimento equivalente fixado em projeto, somado ao das conexões. */
    @Column(name = "l_equivalente_adicional_m", nullable = false)
    private Double lEquivalenteAdicionalM;

    // --- Resultados calculados ---

    @Column(name = "diametro_interno_mm", nullable = false)
    private Double diametroInternoMm;

    @Column(name = "vazao_ls", nullable = false)
    private Double vazaoLs;

    @Column(name = "velocidade_ms", nullable = false)
    private Double velocidadeMs;

    @Column(name = "perda_unitaria_m_m", nullable = false)
    private Double perdaUnitariaMM;

    @Column(name = "l_equivalente_m", nullable = false)
    private Double lEquivalenteM;

    @Column(name = "l_total_m", nullable = false)
    private Double lTotalM;

    @Column(name = "hf_m", nullable = false)
    private Double hfM;

    @Column(name = "pressao_montante_mca", nullable = false)
    private Double pressaoMontanteMca;

    @Column(name = "pressao_jusante_mca", nullable = false)
    private Double pressaoJusanteMca;
}
