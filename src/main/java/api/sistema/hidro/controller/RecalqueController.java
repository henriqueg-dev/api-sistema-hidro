package api.sistema.hidro.controller;

import api.sistema.hidro.dto.RecalqueRequestDTO;
import api.sistema.hidro.dto.RecalqueResponseDTO;
import api.sistema.hidro.service.MemorialPdfService;
import api.sistema.hidro.service.RecalqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recalques")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','ENGENHEIRO')")
public class RecalqueController {

    private final RecalqueService service;
    private final MemorialPdfService memorialPdfService;

    @PostMapping
    public ResponseEntity<RecalqueResponseDTO> criar(@RequestBody @Valid RecalqueRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecalqueResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid RecalqueRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/empreendimento/{empreendimentoId}")
    public ResponseEntity<List<RecalqueResponseDTO>> listarPorEmpreendimento(@PathVariable Long empreendimentoId) {
        return ResponseEntity.ok(service.listarPorEmpreendimento(empreendimentoId));
    }

    @GetMapping("/{id}/memorial.pdf")
    public ResponseEntity<byte[]> memorialPdf(@PathVariable Long id) {
        byte[] pdf = memorialPdfService.gerarMemorialRecalque(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"memorial-recalque-" + id + ".pdf\"")
                .body(pdf);
    }
}
