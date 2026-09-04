package api.sistema.hidro.controller;

import api.sistema.hidro.dto.OrcamentoRequestDTO;
import api.sistema.hidro.dto.OrcamentoResponseDTO;
import api.sistema.hidro.service.MemorialPdfService;
import api.sistema.hidro.service.OrcamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Orçamento comercial do projeto — só ADMIN vê preço/margem, ENGENHEIRO não tem acesso. */
@RestController
@RequestMapping("/api/orcamentos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final MemorialPdfService memorialPdfService;

    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> criar(@RequestBody @Valid OrcamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orcamentoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> atualizar(@PathVariable Long id,
                                                           @RequestBody @Valid OrcamentoRequestDTO dto) {
        return ResponseEntity.ok(orcamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        orcamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrcamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(orcamentoService.listarTodos());
    }

    @GetMapping("/{id}/orcamento.pdf")
    public ResponseEntity<byte[]> orcamentoPdf(@PathVariable Long id) {
        byte[] pdf = memorialPdfService.gerarPdfOrcamento(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"orcamento-" + id + ".pdf\"")
                .body(pdf);
    }
}
