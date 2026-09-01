package api.sistema.hidro.entity;

import api.sistema.hidro.enums.TipoUsoPiscina;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** Recirculação de uma piscina (NBR 10339). Um empreendimento pode ter várias. */
@Entity
@Table(name = "tb_piscina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PiscinaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empreendimento_id", nullable = false)
    private EmpreendimentoEntity empreendimento;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_uso", nullable = false)
    private TipoUsoPiscina tipoUso;

    // --- Entradas: geometria e recirculação ---

    @Column(name = "largura_m", nullable = false)
    private Double larguraM;

    @Column(name = "comprimento_m", nullable = false)
    private Double comprimentoM;

    @Column(name = "profundidade_m", nullable = false)
    private Double profundidadeM;

    @Column(name = "tempo_filtracao_h", nullable = false)
    private Integer tempoFiltracaoH;

    /** Vazão da bomba escolhida em catálogo; deve ser maior ou igual à de projeto. */
    @Column(name = "vazao_bomba_m3h", nullable = false)
    private Double vazaoBombaM3h;

    /** Altura manométrica da bomba de catálogo, pressão de partida do primeiro trecho. */
    @Column(name = "altura_manometrica_mca", nullable = false)
    private Double alturaManometricaMca;

    /** Área servida por skimmer: 50 m² em residencial, 25 m² em piscina pública. */
    @Column(name = "area_por_skimmer_m2", nullable = false)
    private Integer areaPorSkimmerM2;

    // --- Overrides do projetista (nulos usam o valor calculado) ---

    @Column(name = "num_bocais_retorno_adotado")
    private Integer numBocaisRetornoAdotado;

    @Column(name = "num_skimmers_adotado")
    private Integer numSkimmersAdotado;

    @Column(name = "num_ralos_adotado")
    private Integer numRalosAdotado;

    // Valor bruto digitado pelo projetista, antes de resolver contra o calculo. Nulo quando
    // o dispositivo acima foi obtido automaticamente.
    @Column(name = "num_bocais_retorno_informado")
    private Integer numBocaisRetornoInformado;

    @Column(name = "num_skimmers_informado")
    private Integer numSkimmersInformado;

    @Column(name = "num_ralos_informado")
    private Integer numRalosInformado;

    /** Sempre manual: 1 a cada 10 m de raio de alcance, definido no projeto. */
    @Column(name = "num_aspiradores", nullable = false)
    private Integer numAspiradores;

    // --- Resultados calculados ---

    @Column(name = "area_m2", nullable = false)
    private Double areaM2;

    @Column(name = "volume_m3", nullable = false)
    private Double volumeM3;

    @Column(name = "vazao_projeto_m3h", nullable = false)
    private Double vazaoProjetoM3h;

    @Column(name = "dn_recalque_mm", nullable = false)
    private Integer dnRecalqueMm;

    @Column(name = "dn_succao_mm", nullable = false)
    private Integer dnSuccaoMm;

    @Column(name = "velocidade_recalque_ms", nullable = false)
    private Double velocidadeRecalqueMs;

    @Column(name = "velocidade_succao_ms", nullable = false)
    private Double velocidadeSuccaoMs;

    @Column(name = "num_bocais_retorno_calculado", nullable = false)
    private Double numBocaisRetornoCalculado;

    @Column(name = "num_skimmers_calculado", nullable = false)
    private Double numSkimmersCalculado;

    @Column(name = "num_ralos_calculado", nullable = false)
    private Double numRalosCalculado;

    /** Pressão no bocal de retorno mais desfavorável, ao fim da cadeia de trechos. */
    @Column(name = "pressao_residual_mca")
    private Double pressaoResidualMca;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
