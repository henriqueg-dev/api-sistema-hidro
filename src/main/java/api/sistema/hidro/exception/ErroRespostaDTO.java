package api.sistema.hidro.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ErroRespostaDTO {

    private LocalDateTime momento;
    private int status;
    private String erro;
    private String mensagem;
    private Map<String, String> camposInvalidos;

    public ErroRespostaDTO(int status, String erro, String mensagem) {
        this.momento = LocalDateTime.now();
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
    }

    public ErroRespostaDTO(int status, String erro, String mensagem, Map<String, String> camposInvalidos) {
        this(status, erro, mensagem);
        this.camposInvalidos = camposInvalidos;
    }
}
