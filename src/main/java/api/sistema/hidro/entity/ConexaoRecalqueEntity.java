package api.sistema.hidro.entity;

import api.sistema.hidro.enums.TipoConexao;
import api.sistema.hidro.enums.TrechoRecalque;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

/** Conexões da sucção e do recalque, contadas no isométrico do projeto. */
@Audited
@Entity
@Table(name = "tb_conexao_recalque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConexaoRecalqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recalque_id", nullable = false)
    private RecalqueEntity recalque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrechoRecalque trecho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConexao tipo;

    @Column(nullable = false)
    private Integer quantidade;
}
