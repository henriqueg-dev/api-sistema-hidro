package api.sistema.hidro.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ConversaDetalheDTO {
    private ConversaResponseDTO conversa;
    private List<MensagemResponseDTO> mensagens;
}
