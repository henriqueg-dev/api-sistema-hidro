package api.sistema.hidro.controller;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.service.EmpreendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empreendimentos")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class EmpreendimentoController {

    private final EmpreendimentoService empreendimentoService;

    @PostMapping
    public ResponseEntity<EmpreendimentoResponseDTO> criar(
            @RequestBody @Valid EmpreendimentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empreendimentoService.criar(dto));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<EmpreendimentoResponseDTO>> listarPorCliente(
            @PathVariable Long clienteId,
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(empreendimentoService.listarPorCliente(clienteId, busca));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpreendimentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empreendimentoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpreendimentoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid EmpreendimentoRequestDTO dto) {
        return ResponseEntity.ok(empreendimentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        empreendimentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}