package api.sistema.hidro.controller;

import api.sistema.hidro.dto.AssinaturaResponseDTO;
import api.sistema.hidro.dto.CobrancaPixResponseDTO;
import api.sistema.hidro.dto.GerarCobrancaRequestDTO;
import api.sistema.hidro.service.AssinaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assinatura")
@RequiredArgsConstructor
public class AssinaturaController {

    private final AssinaturaService assinaturaService;

    /** Qualquer usuário logado vê o status — inclusive com a assinatura vencida, para saber o motivo. */
    @GetMapping
    public ResponseEntity<AssinaturaResponseDTO> statusAtual() {
        return ResponseEntity.ok(assinaturaService.statusAtual());
    }

    @PostMapping("/gerar-cobranca")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CobrancaPixResponseDTO> gerarCobranca(@RequestBody @Valid GerarCobrancaRequestDTO dto) {
        return ResponseEntity.ok(assinaturaService.gerarCobranca(dto.getPlano()));
    }
}
