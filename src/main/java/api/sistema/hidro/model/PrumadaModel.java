package api.sistema.hidro.model;

import api.sistema.hidro.enums.CondicaoSanca;
import api.sistema.hidro.enums.FaixaPavimentos;
import api.sistema.hidro.enums.TipoPrumada;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPrumada tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "num_pavimentos", nullable = false)
    private FaixaPavimentos numPavimentos;

    @Column(nullable = false)
    private String desconector;

    @Enumerated(EnumType.STRING)
    @Column
    private CondicaoSanca condicaoSanca;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Builder.Default
    private Boolean ativo = true;
}