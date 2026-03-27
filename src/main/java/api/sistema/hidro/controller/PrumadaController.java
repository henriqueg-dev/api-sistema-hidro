package api.sistema.hidro.controller;

import api.sistema.hidro.dto.PrumadaConsultaDTO;
import api.sistema.hidro.dto.PrumadaRespostaDTO;
import api.sistema.hidro.model.PrumadaModel;
import api.sistema.hidro.service.PrumadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prumadas")
@RequiredArgsConstructor
public class PrumadaController {

    private final PrumadaService prumadaService;

    @PostMapping("/consultar")
    public ResponseEntity<PrumadaRespostaDTO> consultar(
            @RequestBody PrumadaConsultaDTO dto) {
        return ResponseEntity.ok(prumadaService.consultar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PrumadaModel>> listar(@RequestParam String tipo) {
        return ResponseEntity.ok(prumadaService.listarTodos(tipo));
    }
}