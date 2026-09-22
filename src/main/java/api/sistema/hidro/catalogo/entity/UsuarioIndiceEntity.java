package api.sistema.hidro.catalogo.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Resolve qual banco de tenant consultar a partir do e-mail digitado no login — a senha em si
 * não mora aqui, só no banco do tenant. Mantido em sincronia com {@code UsuarioEntity} (do
 * tenant) toda vez que um usuário é criado ou removido.
 */
@Entity
@Table(name = "tb_usuario_indice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioIndiceEntity {

    @Id
    private String email;

    @Column(name = "conta_id", nullable = false)
    private Long contaId;
}
