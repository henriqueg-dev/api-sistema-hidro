package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RevisaoResponseDTO {
    private int revisao;
    private LocalDateTime dataOperacao;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    private List<AlteracaoDTO> alteracoes;
}
