package api.sistema.hidro.service;

import api.sistema.hidro.dto.VazaoPredialRequestDTO;
import api.sistema.hidro.dto.VazaoPredialResponseDTO;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.VazaoPredialEntity;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.VazaoPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VazaoPredialService {

    /** Cada empreendimento tem um único cálculo de vazão predial. */
    public static final int MAX_POR_EMPREENDIMENTO = 1;

    /** Coeficiente do dia de maior consumo. */
    public static final double K1 = 1.2;

    /** Coeficiente da hora de maior consumo. */
    public static final double K2 = 1.5;

    private static final int SEGUNDOS_POR_DIA = 86400;
    private static final int SEGUNDOS_POR_HORA = 3600;
    private static final int LITROS_POR_M3 = 1000;

    /** 365 dias x 24 h / 12 meses. */
    private static final double HORAS_POR_MES = 730;

    private final VazaoPredialRepository vazaoPredialRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public VazaoPredialResponseDTO criar(VazaoPredialRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        if (vazaoPredialRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui um cálculo de vazão predial. Altere o cálculo existente.");
        }

        VazaoPredialEntity vazao = VazaoPredialEntity.builder()
                .empreendimento(empreendimento)
                .build();

        aplicarCalculo(vazao, dto);
        vazaoPredialRepository.save(vazao);
        return toDTO(vazao);
    }

    @Transactional
    public VazaoPredialResponseDTO atualizar(Long id, VazaoPredialRequestDTO dto) {
        VazaoPredialEntity vazao = buscarEntidade(id);
        aplicarCalculo(vazao, dto);
        vazaoPredialRepository.save(vazao);
        return toDTO(vazao);
    }

    @Transactional
    public void excluir(Long id) {
        vazaoPredialRepository.delete(buscarEntidade(id));
    }

    public List<VazaoPredialResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return vazaoPredialRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void aplicarCalculo(VazaoPredialEntity vazao, VazaoPredialRequestDTO dto) {
        int populacao = dto.getTaxaOcupacao() * dto.getNumApartamentos();
        double consumoDiarioLitros = (double) populacao * dto.getConsumoPerCapita();

        double vazaoMedia = consumoDiarioLitros / SEGUNDOS_POR_DIA;
        double vazaoMaximaDiaria = vazaoMedia * K1;

        vazao.setTaxaOcupacao(dto.getTaxaOcupacao());
        vazao.setNumApartamentos(dto.getNumApartamentos());
        vazao.setConsumoPerCapita(dto.getConsumoPerCapita());
        vazao.setCapacidadeEquivalenteDias(dto.getCapacidadeEquivalenteDias());

        vazao.setPopulacao(populacao);
        vazao.setVolumeCaixaM3(
                consumoDiarioLitros * dto.getCapacidadeEquivalenteDias() / LITROS_POR_M3);
        vazao.setVazaoMediaLps(vazaoMedia);
        vazao.setVazaoMaximaDiariaLps(vazaoMaximaDiaria);
        vazao.setVazaoMaximaHoraLps(vazaoMaximaDiaria * K2);
    }

    private VazaoPredialEntity buscarEntidade(Long id) {
        return vazaoPredialRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de vazão predial não encontrado"));
    }

    private VazaoPredialResponseDTO toDTO(VazaoPredialEntity v) {
        double vazaoHoraM3h = v.getVazaoMaximaHoraLps() * SEGUNDOS_POR_HORA / LITROS_POR_M3;

        return new VazaoPredialResponseDTO(
                v.getId(),
                v.getEmpreendimento().getId(),
                v.getTaxaOcupacao(),
                v.getNumApartamentos(),
                v.getConsumoPerCapita(),
                v.getCapacidadeEquivalenteDias(),
                K1,
                K2,
                v.getPopulacao(),
                v.getVolumeCaixaM3(),
                v.getVazaoMediaLps(),
                v.getVazaoMaximaDiariaLps(),
                v.getVazaoMaximaHoraLps(),
                vazaoHoraM3h,
                vazaoHoraM3h * HORAS_POR_MES,
                v.getCriadoEm(),
                v.getAtualizadoEm());
    }
}
