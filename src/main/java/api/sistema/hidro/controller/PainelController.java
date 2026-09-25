package api.sistema.hidro.controller;

import api.sistema.hidro.dto.PainelDTO;
import api.sistema.hidro.service.PainelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/painel")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class PainelController {

    private final PainelService painelService;

    @GetMapping
    public ResponseEntity<PainelDTO> montar(Authentication autenticacao) {
        boolean administrador = autenticacao.getAuthorities().stream()
                .anyMatch(autoridade -> "ROLE_ADMIN".equals(autoridade.getAuthority()));
        return ResponseEntity.ok(painelService.montar(administrador));
    }
}
