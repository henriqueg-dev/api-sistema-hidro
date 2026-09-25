package api.sistema.hidro.controller;

import api.sistema.hidro.dto.TabelaNormativaDTO;
import api.sistema.hidro.service.TabelaNormativaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tabelas-normativas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class TabelaNormativaController {

    private final TabelaNormativaService tabelaNormativaService;

    @GetMapping
    public ResponseEntity<List<TabelaNormativaDTO>> listar() {
        return ResponseEntity.ok(tabelaNormativaService.listar());
    }
}
