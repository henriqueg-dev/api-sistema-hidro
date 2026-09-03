package api.sistema.hidro.service;

import api.sistema.hidro.dto.PiscinaResponseDTO;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Gera o memorial de cálculo em PDF a partir do mesmo dado já salvo e calculado. */
@Service
@RequiredArgsConstructor
public class MemorialPdfService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PiscinaService piscinaService;
    private final EmpreendimentoRepository empreendimentoRepository;
    private final TemplateEngine templateEngine;

    @Transactional(readOnly = true)
    public byte[] gerarMemorialPiscina(Long piscinaId) {
        PiscinaResponseDTO piscina = piscinaService.buscarPorId(piscinaId);
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(piscina.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        Context contexto = new Context();
        contexto.setVariable("piscina", piscina);
        contexto.setVariable("empreendimentoNome", empreendimento.getNome());
        contexto.setVariable("clienteNome", empreendimento.getCliente().getNome());
        contexto.setVariable("dataGeracao", FORMATO_DATA.format(LocalDateTime.now()));

        String html = templateEngine.process("memorial-piscina", contexto);
        return renderizarPdf(html);
    }

    private byte[] renderizarPdf(String html) {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(saida);
            builder.run();
            return saida.toByteArray();
        } catch (IOException e) {
            throw new RegraNegocioException("Falha ao gerar o PDF do memorial de cálculo");
        }
    }
}
