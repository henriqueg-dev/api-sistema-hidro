package api.sistema.hidro.entity;

import api.sistema.hidro.enums.TipoConexao;
import jakarta.persistence.*;
import lombok.*;

/** Conexões de um trecho, contadas no isométrico do projeto. */
@Entity
@Table(name = "tb_conexao_trecho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConexaoTrechoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trecho_id", nullable = false)
    private TrechoPiscinaEntity trecho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConexao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    /** Comprimento equivalente total desta linha: quantidade x tabela do DN. */
    @Column(name = "comprimento_equivalente_m", nullable = false)
    private Double comprimentoEquivalenteM;
}
