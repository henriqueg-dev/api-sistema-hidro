package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class AuditoriaResponseDTO {
    private int revisao;
    private LocalDateTime dataOperacao;
    private String acao;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;
    /** Campos simples da entidade como estavam nessa revisão. */
    private Map<String, Object> valores;
}
