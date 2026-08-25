package api.sistema.hidro.assistente.controller;

import api.sistema.hidro.assistente.dto.ConversaDetalheDTO;
import api.sistema.hidro.assistente.dto.ConversaResponseDTO;
import api.sistema.hidro.assistente.dto.MensagemRequestDTO;
import api.sistema.hidro.assistente.dto.MensagemResponseDTO;
import api.sistema.hidro.assistente.service.AssistenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Assistente técnico. Todas as conversas são do usuário autenticado — o e-mail vem
 * do token JWT, nunca do corpo da requisição.
 */
@RestController
@RequestMapping("/api/assistente")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class AssistenteController {

    private final AssistenteService assistenteService;

    /** Permite à tela avisar que falta configurar a chave, antes de o usuário digitar. */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> status() {
        return ResponseEntity.ok(Map.of("configurado", assistenteService.assistenteConfigurado()));
    }

    @GetMapping("/conversas")
    public ResponseEntity<List<ConversaResponseDTO>> listarConversas(Authentication authentication) {
        return ResponseEntity.ok(assistenteService.listarConversas(authentication.getName()));
    }

    @GetMapping("/conversas/{id}")
    public ResponseEntity<ConversaDetalheDTO> buscarConversa(@PathVariable Long id,
                                                             Authentication authentication) {
        return ResponseEntity.ok(assistenteService.buscarConversa(id, authentication.getName()));
    }

    @PostMapping("/conversas")
    public ResponseEntity<ConversaDetalheDTO> criarConversa(@RequestBody @Valid MensagemRequestDTO dto,
                                                            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assistenteService.criarConversa(dto, authentication.getName()));
    }

    @PostMapping("/conversas/{id}/mensagens")
    public ResponseEntity<MensagemResponseDTO> enviarMensagem(@PathVariable Long id,
                                                              @RequestBody @Valid MensagemRequestDTO dto,
                                                              Authentication authentication) {
        return ResponseEntity.ok(assistenteService.enviarMensagem(id, dto, authentication.getName()));
    }

    @DeleteMapping("/conversas/{id}")
    public ResponseEntity<Void> excluirConversa(@PathVariable Long id, Authentication authentication) {
        assistenteService.excluirConversa(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
