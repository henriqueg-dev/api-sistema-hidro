package api.sistema.hidro.controller;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.service.EmpreendimentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empreendimentos")
@RequiredArgsConstructor
public class EmpreendimentoController {

    private final EmpreendimentoService empreendimentoService;

    @PostMapping
    public ResponseEntity<EmpreendimentoResponseDTO> criar(
            @RequestBody @Valid EmpreendimentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empreendimentoService.criar(dto));
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<EmpreendimentoResponseDTO>> listarPorEmpresa(
            @PathVariable Long empresaId) {
        return ResponseEntity.ok(empreendimentoService.listarPorEmpresa(empresaId));
    }
}