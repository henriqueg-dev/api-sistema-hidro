package api.sistema.hidro.controller;

import api.sistema.hidro.dto.RamalPredialRequestDTO;
import api.sistema.hidro.dto.RamalPredialResponseDTO;
import api.sistema.hidro.service.RamalPredialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ramais-prediais")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class RamalPredialController {

    private final RamalPredialService ramalPredialService;

    @PostMapping
    public ResponseEntity<RamalPredialResponseDTO> criar(
            @RequestBody @Valid RamalPredialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ramalPredialService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RamalPredialResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RamalPredialRequestDTO dto) {
        return ResponseEntity.ok(ramalPredialService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        ramalPredialService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<RamalPredialResponseDTO>> listarPorEmpreendimento(
            @PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(ramalPredialService.listarPorEmpreendimento(empreendimentoId));
    }
}
