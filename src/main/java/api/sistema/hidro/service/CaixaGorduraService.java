package api.sistema.hidro.service;

import api.sistema.hidro.dto.CaixaGorduraRequestDTO;
import api.sistema.hidro.dto.CaixaGorduraResponseDTO;
import api.sistema.hidro.entity.CaixaGorduraEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.CaixaGorduraRepository;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaixaGorduraService {

    // Volume da caixa de gordura e sabão: V = 2 x N + 20, em litros,
    // onde N é a população atendida (taxa de ocupação x número de apartamentos).
    public static final String FORMULA = "V = 2xN + 20";
    private static final int FATOR_POPULACAO = 2;
    private static final int PARCELA_FIXA_LITROS = 20;

    /** Cada empreendimento pode ter no máximo dois cálculos de caixa de gordura e sabão. */
    public static final int MAX_POR_EMPREENDIMENTO = 2;

    private final CaixaGorduraRepository caixaGorduraRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public CaixaGorduraResponseDTO criar(CaixaGorduraRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        if (caixaGorduraRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui " + MAX_POR_EMPREENDIMENTO
                            + " cálculos de caixa de gordura e sabão. Altere um dos cálculos existentes.");
        }

        CaixaGorduraEntity caixaGordura = CaixaGorduraEntity.builder()
                .empreendimento(empreendimento)
                .build();

        aplicarCalculo(caixaGordura, dto);
        caixaGorduraRepository.save(caixaGordura);
        return toDTO(caixaGordura);
    }

    @Transactional
    public CaixaGorduraResponseDTO atualizar(Long id, CaixaGorduraRequestDTO dto) {
        CaixaGorduraEntity caixaGordura = caixaGorduraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de caixa de gordura não encontrado"));

        aplicarCalculo(caixaGordura, dto);
        caixaGorduraRepository.save(caixaGordura);
        return toDTO(caixaGordura);
    }

    @Transactional
    public void excluir(Long id) {
        CaixaGorduraEntity caixaGordura = caixaGorduraRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de caixa de gordura não encontrado"));

        caixaGorduraRepository.delete(caixaGordura);
    }

    public List<CaixaGorduraResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return caixaGorduraRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void aplicarCalculo(CaixaGorduraEntity caixaGordura, CaixaGorduraRequestDTO dto) {
        int populacao = dto.getTaxaOcupacao() * dto.getNumApartamentos();

        caixaGordura.setTaxaOcupacao(dto.getTaxaOcupacao());
        caixaGordura.setNumApartamentos(dto.getNumApartamentos());
        caixaGordura.setPopulacao(populacao);
        caixaGordura.setVolumeLitros(FATOR_POPULACAO * populacao + PARCELA_FIXA_LITROS);
    }

    private CaixaGorduraResponseDTO toDTO(CaixaGorduraEntity c) {
        return new CaixaGorduraResponseDTO(
                c.getId(),
                c.getEmpreendimento().getId(),
                c.getTaxaOcupacao(),
                c.getNumApartamentos(),
                c.getPopulacao(),
                FORMULA,
                c.getVolumeLitros(),
                c.getCriadoEm(),
                c.getAtualizadoEm());
    }
}
