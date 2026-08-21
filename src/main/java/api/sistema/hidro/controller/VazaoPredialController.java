package api.sistema.hidro.controller;

import api.sistema.hidro.dto.VazaoPredialRequestDTO;
import api.sistema.hidro.dto.VazaoPredialResponseDTO;
import api.sistema.hidro.service.VazaoPredialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vazoes-prediais")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class VazaoPredialController {

    private final VazaoPredialService vazaoPredialService;

    @PostMapping
    public ResponseEntity<VazaoPredialResponseDTO> criar(
            @RequestBody @Valid VazaoPredialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vazaoPredialService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VazaoPredialResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid VazaoPredialRequestDTO dto) {
        return ResponseEntity.ok(vazaoPredialService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        vazaoPredialService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<VazaoPredialResponseDTO>> listarPorEmpreendimento(
            @PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(vazaoPredialService.listarPorEmpreendimento(empreendimentoId));
    }
}
