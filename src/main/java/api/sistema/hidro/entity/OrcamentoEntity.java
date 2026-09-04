package api.sistema.hidro.entity;

import api.sistema.hidro.enums.StatusOrcamento;
import api.sistema.hidro.enums.TipoEmpreendimento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "tb_orcamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrcamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;

    @Column(name = "nome_empreendimento", nullable = false)
    private String nomeEmpreendimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_empreendimento", nullable = false)
    private TipoEmpreendimento tipoEmpreendimento;

    @Column(nullable = false)
    private Double quantidade;

    @Column(name = "valor_unitario", nullable = false)
    private Double valorUnitario;

    @Column(name = "valor_total", nullable = false)
    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOrcamento status;

    @Column(columnDefinition = "text")
    private String observacoes;

    @Column(name = "validade_dias", nullable = false)
    private Integer validadeDias;

    @Column(name = "empreendimento_gerado_id")
    private Long empreendimentoGeradoId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
