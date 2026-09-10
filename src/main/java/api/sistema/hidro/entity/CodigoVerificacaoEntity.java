package api.sistema.hidro.entity;

import api.sistema.hidro.enums.FinalidadeCodigo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_codigo_verificacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodigoVerificacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private UsuarioEntity usuario;

    @Column(nullable = false)
    private String codigoHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FinalidadeCodigo finalidade;

    @Column(nullable = false)
    private LocalDateTime expiraEm;

    @Builder.Default
    private int tentativas = 0;
}
