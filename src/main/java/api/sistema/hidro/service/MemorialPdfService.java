package api.sistema.hidro.service;

import api.sistema.hidro.dto.PiscinaResponseDTO;
import api.sistema.hidro.dto.RamalPredialResponseDTO;
import api.sistema.hidro.dto.TanqueSepticoResponseDTO;
import api.sistema.hidro.dto.VazaoPredialResponseDTO;
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

/** Gera os memoriais de cálculo em PDF a partir do mesmo dado já salvo e calculado. */
@Service
@RequiredArgsConstructor
public class MemorialPdfService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final PiscinaService piscinaService;
    private final RamalPredialService ramalPredialService;
    private final TanqueSepticoService tanqueSepticoService;
    private final VazaoPredialService vazaoPredialService;
    private final EmpreendimentoRepository empreendimentoRepository;
    private final TemplateEngine templateEngine;

    @Transactional(readOnly = true)
    public byte[] gerarMemorialPiscina(Long piscinaId) {
        PiscinaResponseDTO piscina = piscinaService.buscarPorId(piscinaId);
        Context contexto = contextoBase(piscina.getEmpreendimentoId());
        contexto.setVariable("piscina", piscina);
        return renderizarPdf("memorial-piscina", contexto);
    }

    @Transactional(readOnly = true)
    public byte[] gerarMemorialRamalPredial(Long id) {
        RamalPredialResponseDTO ramal = ramalPredialService.buscarPorId(id);
        Context contexto = contextoBase(ramal.getEmpreendimentoId());
        contexto.setVariable("ramal", ramal);
        contexto.setVariable("hidrometroRotulo", RotulosPdf.hidrometro(ramal.getHidrometro()));
        contexto.setVariable("concessionariaRotulo", RotulosPdf.concessionaria(ramal.getConcessionaria()));
        return renderizarPdf("memorial-ramal-predial", contexto);
    }

    @Transactional(readOnly = true)
    public byte[] gerarMemorialTanqueSeptico(Long id) {
        TanqueSepticoResponseDTO tanque = tanqueSepticoService.buscarPorId(id);
        Context contexto = contextoBase(tanque.getEmpreendimentoId());
        contexto.setVariable("tanque", tanque);
        contexto.setVariable("contribuicaoRotulo", RotulosPdf.contribuicaoDespejo(tanque.getContribuicaoDespejo()));
        contexto.setVariable("unidadeRotulo", RotulosPdf.unidadeContribuicao(tanque.getUnidadeContribuicao()));
        contexto.setVariable("formaRotulo", RotulosPdf.formaTanque(tanque.getFormaTanque()));
        contexto.setVariable("faixaRotulo", RotulosPdf.faixaTemperatura(tanque.getFaixaTemperatura()));
        return renderizarPdf("memorial-tanque-septico", contexto);
    }

    @Transactional(readOnly = true)
    public byte[] gerarMemorialVazaoPredial(Long id) {
        VazaoPredialResponseDTO vazao = vazaoPredialService.buscarPorId(id);
        Context contexto = contextoBase(vazao.getEmpreendimentoId());
        contexto.setVariable("vazao", vazao);
        return renderizarPdf("memorial-vazao-predial", contexto);
    }

    /** Cabeçalho comum a todo memorial: nome do cliente/empreendimento e data de geração. */
    private Context contextoBase(Long empreendimentoId) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(empreendimentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        Context contexto = new Context();
        contexto.setVariable("empreendimentoNome", empreendimento.getNome());
        contexto.setVariable("clienteNome", empreendimento.getCliente().getNome());
        contexto.setVariable("dataGeracao", FORMATO_DATA.format(LocalDateTime.now()));
        return contexto;
    }

    private byte[] renderizarPdf(String nomeTemplate, Context contexto) {
        String html = templateEngine.process(nomeTemplate, contexto);
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
