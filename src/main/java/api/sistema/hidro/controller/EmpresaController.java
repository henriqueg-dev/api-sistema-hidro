package api.sistema.hidro.controller;

import api.sistema.hidro.dto.EmpresaRequestDTO;
import api.sistema.hidro.dto.EmpresaResponseDTO;
import api.sistema.hidro.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> criar(@RequestBody @Valid EmpresaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.buscarPorId(id));
    }
}