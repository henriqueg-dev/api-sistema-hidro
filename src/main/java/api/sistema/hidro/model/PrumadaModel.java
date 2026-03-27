package api.sistema.hidro.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_prumada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrumadaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String numPavimentos;

    @Column(nullable = false)
    private String desconector;

    @Column
    private String condicaoSanca;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Builder.Default
    private Boolean ativo = true;
}