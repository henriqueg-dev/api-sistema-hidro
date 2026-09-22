package api.sistema.hidro.catalogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Um escritório do sistema. Só metadado de conta e billing mora aqui — os dados de negócio
 * (Cliente, Empreendimento, cálculos etc.) ficam no banco próprio dessa conta, provisionado por
 * {@code TenantProvisionamentoService} a partir das migrations em db/tenant.
 */
@Entity
@Table(name = "tb_conta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_escritorio", nullable = false)
    private String nomeEscritorio;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;
}
