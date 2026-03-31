package api.sistema.hidro.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_empreendimento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpreendimentoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private Integer numPavimentos;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String concessionaria;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private EmpresaModel empresa;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    private LocalDateTime criadoEm;
}