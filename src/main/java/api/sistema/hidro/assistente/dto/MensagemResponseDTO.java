package api.sistema.hidro.assistente.dto;

import api.sistema.hidro.assistente.enums.PapelMensagem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MensagemResponseDTO {
    private Long id;
    private PapelMensagem papel;
    private String conteudo;
    private LocalDateTime criadoEm;
}
