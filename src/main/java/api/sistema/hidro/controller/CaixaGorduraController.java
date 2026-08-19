package api.sistema.hidro.controller;

import api.sistema.hidro.dto.CaixaGorduraRequestDTO;
import api.sistema.hidro.dto.CaixaGorduraResponseDTO;
import api.sistema.hidro.service.CaixaGorduraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caixas-gordura")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class CaixaGorduraController {

    private final CaixaGorduraService caixaGorduraService;

    @PostMapping
    public ResponseEntity<CaixaGorduraResponseDTO> criar(
            @RequestBody @Valid CaixaGorduraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caixaGorduraService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaixaGorduraResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CaixaGorduraRequestDTO dto) {
        return ResponseEntity.ok(caixaGorduraService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        caixaGorduraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<CaixaGorduraResponseDTO>> listarPorEmpreendimento(
            @PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(caixaGorduraService.listarPorEmpreendimento(empreendimentoId));
    }
}
