package api.sistema.hidro.assistente.entity;

import api.sistema.hidro.assistente.enums.PapelMensagem;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_mensagem")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensagemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversa_id", nullable = false)
    private ConversaEntity conversa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PapelMensagem papel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;
}
