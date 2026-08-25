package api.sistema.hidro.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Conversa do usuário com o assistente técnico. Quando ligada a um empreendimento,
 * os dados desse projeto são enviados ao modelo como contexto a cada pergunta.
 */
@Entity
@Table(name = "tb_conversa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    /** Opcional: conversa sem empreendimento é uma dúvida técnica geral. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empreendimento_id")
    private EmpreendimentoEntity empreendimento;

    @Column(nullable = false)
    private String titulo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
