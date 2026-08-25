package api.sistema.hidro.assistente.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ConversaResponseDTO {
    private Long id;
    private String titulo;
    private Long empreendimentoId;
    private String empreendimentoNome;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
