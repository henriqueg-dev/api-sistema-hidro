package api.sistema.hidro.controller;

import api.sistema.hidro.dto.TanqueSepticoRequestDTO;
import api.sistema.hidro.dto.TanqueSepticoResponseDTO;
import api.sistema.hidro.service.TanqueSepticoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tanques-septicos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class TanqueSepticoController {

    private final TanqueSepticoService tanqueSepticoService;

    @PostMapping
    public ResponseEntity<TanqueSepticoResponseDTO> criar(
            @RequestBody @Valid TanqueSepticoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tanqueSepticoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TanqueSepticoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid TanqueSepticoRequestDTO dto) {
        return ResponseEntity.ok(tanqueSepticoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        tanqueSepticoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<TanqueSepticoResponseDTO>> listarPorEmpreendimento(
            @PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(tanqueSepticoService.listarPorEmpreendimento(empreendimentoId));
    }
}
