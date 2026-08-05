package api.sistema.hidro.dto;

import api.sistema.hidro.enums.TipoEmpreendimento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EmpreendimentoResponseDTO {
    private Long id;
    private String nome;
    private TipoEmpreendimento tipo;
    private Integer numPavimentos;
    private String endereco;
    private String concessionaria;
    private Long empresaId;
    private String empresaNome;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}