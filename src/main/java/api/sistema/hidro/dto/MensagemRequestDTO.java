package api.sistema.hidro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemRequestDTO {

    /**
     * Opcional e usado apenas ao abrir a conversa: liga o histórico a um empreendimento
     * para que os dados e cálculos dele sejam enviados como contexto.
     */
    private Long empreendimentoId;

    @NotBlank(message = "Mensagem é obrigatória")
    @Size(max = 4000, message = "Mensagem deve ter no máximo 4000 caracteres")
    private String mensagem;
}
