package api.sistema.hidro.controller;

import api.sistema.hidro.dto.PiscinaReferenciasDTO;
import api.sistema.hidro.dto.PiscinaRequestDTO;
import api.sistema.hidro.dto.PiscinaResponseDTO;
import api.sistema.hidro.service.PiscinaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/piscinas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class PiscinaController {

    private final PiscinaService piscinaService;

    /** Tabelas da NBR 10339 e de comprimento equivalente usadas pela tela. */
    @GetMapping("/referencias")
    public ResponseEntity<PiscinaReferenciasDTO> referencias() {
        return ResponseEntity.ok(piscinaService.referencias());
    }

    @PostMapping
    public ResponseEntity<PiscinaResponseDTO> criar(@RequestBody @Valid PiscinaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(piscinaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PiscinaResponseDTO> atualizar(@PathVariable Long id,
                                                        @RequestBody @Valid PiscinaRequestDTO dto) {
        return ResponseEntity.ok(piscinaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        piscinaService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PiscinaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(piscinaService.buscarPorId(id));
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<PiscinaResponseDTO>> listarPorEmpreendimento(
            @PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(piscinaService.listarPorEmpreendimento(empreendimentoId));
    }
}
