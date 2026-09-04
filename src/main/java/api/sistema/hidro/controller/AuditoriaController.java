package api.sistema.hidro.controller;

import api.sistema.hidro.dto.AuditoriaResponseDTO;
import api.sistema.hidro.dto.RevisaoResponseDTO;
import api.sistema.hidro.enums.EntidadeAuditavel;
import api.sistema.hidro.service.AuditoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Histórico de alterações: só ADMIN consulta quem mexeu no quê. */
@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<RevisaoResponseDTO>> linhaDoTempo(
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(auditoriaService.linhaDoTempo(busca));
    }

    @GetMapping("/{entidade}/{id}")
    public ResponseEntity<List<AuditoriaResponseDTO>> historico(@PathVariable EntidadeAuditavel entidade,
                                                                @PathVariable Long id) {
        return ResponseEntity.ok(auditoriaService.historico(entidade, id));
    }
}
