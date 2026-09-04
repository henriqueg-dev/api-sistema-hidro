package api.sistema.hidro.entity;

import api.sistema.hidro.security.RevisaoListener;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

/** Uma linha por alteração auditada: além do número e do instante, guarda quem fez. */
@Entity
@Table(name = "tb_revisao")
@RevisionEntity(RevisaoListener.class)
@Getter
@Setter
public class RevisaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    @Column(name = "data_operacao")
    private LocalDateTime dataOperacao;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_nome")
    private String usuarioNome;

    @Column(name = "usuario_email")
    private String usuarioEmail;

    @ElementCollection(fetch = FetchType.EAGER)
    @JoinTable(name = "tb_revisao_entidade", joinColumns = @JoinColumn(name = "rev"))
    @Column(name = "entidade")
    @ModifiedEntityNames
    private Set<String> entidadesAlteradas;
}
