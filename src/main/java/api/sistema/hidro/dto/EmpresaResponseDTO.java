package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class EmpresaResponseDTO {
    private Long id;
    private String nome;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}