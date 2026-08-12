package api.sistema.hidro.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final String MSG_CREDENCIAIS_INVALIDAS = "Email ou senha inválidos";
    public static final String MSG_ACESSO_NEGADO = "Acesso negado para o perfil informado";
    public static final String MSG_USUARIO_DESATIVADO = "Usuário desativado";

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroRespostaDTO> tratarRegraNegocio(RegraNegocioException ex) {
        return construirResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroRespostaDTO> tratarCredenciaisInvalidas(BadCredentialsException ex) {
        return construirResposta(HttpStatus.UNAUTHORIZED, MSG_CREDENCIAIS_INVALIDAS);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErroRespostaDTO> tratarUsuarioDesativado(DisabledException ex) {
        return construirResposta(HttpStatus.FORBIDDEN, MSG_USUARIO_DESATIVADO);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErroRespostaDTO> tratarFalhaAutenticacao(AuthenticationException ex) {
        return construirResposta(HttpStatus.UNAUTHORIZED, MSG_CREDENCIAIS_INVALIDAS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroRespostaDTO> tratarAcessoNegado(AccessDeniedException ex) {
        return construirResposta(HttpStatus.FORBIDDEN, MSG_ACESSO_NEGADO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroRespostaDTO> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> camposInvalidos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            camposInvalidos.put(erro.getField(), erro.getDefaultMessage());
        }
        ErroRespostaDTO corpo = new ErroRespostaDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Dados inválidos informados na requisição",
                camposInvalidos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroRespostaDTO> tratarCorpoInvalido(HttpMessageNotReadableException ex) {
        return construirResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroRespostaDTO> tratarErroGenerico(Exception ex) {
        log.error("Erro inesperado", ex);
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado no servidor");
    }

    private ResponseEntity<ErroRespostaDTO> construirResposta(HttpStatus status, String mensagem) {
        ErroRespostaDTO corpo = new ErroRespostaDTO(status.value(), status.getReasonPhrase(), mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
