package api.sistema.hidro.controller;

import api.sistema.hidro.dto.PrumadaConsultaDTO;
import api.sistema.hidro.dto.PrumadaRespostaDTO;
import api.sistema.hidro.enums.TipoPrumada;
import api.sistema.hidro.service.PrumadaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prumadas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class PrumadaController {

    private final PrumadaService prumadaService;

    @PostMapping("/consultar")
    public ResponseEntity<PrumadaRespostaDTO> consultar(
            @RequestBody @Valid PrumadaConsultaDTO dto) {
        return ResponseEntity.ok(prumadaService.consultar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrumadaRespostaDTO>> listar(@RequestParam TipoPrumada tipo) {
        return ResponseEntity.ok(prumadaService.listarTodos(tipo));
    }
}