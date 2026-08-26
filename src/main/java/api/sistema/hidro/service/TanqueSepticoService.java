package api.sistema.hidro.service;

import api.sistema.hidro.dto.TanqueSepticoRequestDTO;
import api.sistema.hidro.dto.TanqueSepticoResponseDTO;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.TanqueSepticoEntity;
import api.sistema.hidro.enums.ContribuicaoDespejo;
import api.sistema.hidro.enums.FormaTanque;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.TanqueSepticoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TanqueSepticoService {

    // Volume útil do tanque séptico: V = 1000 + N x (C x T + K x Lf), em litros.
    public static final String FORMULA = "V = 1000 + N x (C x T + K x Lf)";

    /** Parcela fixa da fórmula da NBR 7229, em litros. */
    private static final int PARCELA_FIXA_LITROS = 1000;

    /**
     * Volume útil mínimo adotado, em litros. Valor consagrado na prática de projeto e citado na
     * literatura como piso do tanque séptico; não consta da seção 5.9 da NBR 7229, que fixa
     * apenas profundidade, diâmetro, largura e relação comprimento/largura.
     */
    public static final int VOLUME_MINIMO_LITROS = 1250;

    public static final int VAZAO_MAXIMA_LITROS_DIA = 12_000;

    /** Cada empreendimento tem um único cálculo de tanque séptico. */
    public static final int MAX_POR_EMPREENDIMENTO = 1;

    private static final int LITROS_POR_M3 = 1000;

    private final TanqueSepticoRepository tanqueSepticoRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public TanqueSepticoResponseDTO criar(TanqueSepticoRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        if (tanqueSepticoRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui um cálculo de tanque séptico. Altere o cálculo existente.");
        }

        TanqueSepticoEntity tanque = TanqueSepticoEntity.builder()
                .empreendimento(empreendimento)
                .build();

        aplicarCalculo(tanque, dto);
        tanqueSepticoRepository.save(tanque);
        return toDTO(tanque);
    }

    @Transactional
    public TanqueSepticoResponseDTO atualizar(Long id, TanqueSepticoRequestDTO dto) {
        TanqueSepticoEntity tanque = buscarEntidade(id);

        aplicarCalculo(tanque, dto);
        tanqueSepticoRepository.save(tanque);
        return toDTO(tanque);
    }

    @Transactional
    public void excluir(Long id) {
        tanqueSepticoRepository.delete(buscarEntidade(id));
    }

    public List<TanqueSepticoResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return tanqueSepticoRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void aplicarCalculo(TanqueSepticoEntity tanque, TanqueSepticoRequestDTO dto) {
        ContribuicaoDespejo contribuicao = dto.getContribuicaoDespejo();

        int populacao = dto.getTaxaOcupacao() * dto.getNumUnidades();
        int contribuicaoDiaria = populacao * contribuicao.getContribuicaoLitros();

        double periodoDetencao = periodoDetencaoDias(contribuicaoDiaria);
        int taxaAcumulacao = dto.getFaixaTemperatura().taxaAcumulacaoLodo(dto.getIntervaloLimpezaAnos());

        double volume = PARCELA_FIXA_LITROS + populacao
                * (contribuicao.getContribuicaoLitros() * periodoDetencao
                        + taxaAcumulacao * contribuicao.getLodoFrescoLitros());

        int volumeCalculado = (int) Math.round(volume);

        tanque.setTaxaOcupacao(dto.getTaxaOcupacao());
        tanque.setNumUnidades(dto.getNumUnidades());
        tanque.setContribuicaoDespejo(contribuicao);
        tanque.setFaixaTemperatura(dto.getFaixaTemperatura());
        tanque.setIntervaloLimpezaAnos(dto.getIntervaloLimpezaAnos());

        tanque.setPopulacao(populacao);
        tanque.setContribuicaoDiariaLitros(contribuicaoDiaria);
        tanque.setPeriodoDetencaoDias(periodoDetencao);
        tanque.setTaxaAcumulacaoDias(taxaAcumulacao);
        tanque.setVolumeCalculadoLitros(volumeCalculado);
        tanque.setVolumeLitros(Math.max(volumeCalculado, VOLUME_MINIMO_LITROS));

        aplicarGeometria(tanque, dto);
    }

    /**
     * A geometria é resolvida sobre o volume útil adotado — ou seja, sobre o mínimo normativo
     * quando ele prevalece, e não sobre o volume que a fórmula devolveu.
     */
    private void aplicarGeometria(TanqueSepticoEntity tanque, TanqueSepticoRequestDTO dto) {
        FormaTanque forma = dto.getFormaTanque() != null
                ? dto.getFormaTanque()
                : FormaTanque.PRISMATICO_RETANGULAR;

        GeometriaTanqueSeptico.Resultado geometria = GeometriaTanqueSeptico.resolver(
                tanque.getVolumeLitros() / (double) LITROS_POR_M3,
                forma,
                dto.getProfundidadeUtilM());

        tanque.setFormaTanque(geometria.forma());
        tanque.setProfundidadeInformadaM(dto.getProfundidadeUtilM());
        tanque.setProfundidadeUtilM(geometria.profundidadeUtilM());
        tanque.setLarguraM(geometria.larguraM());
        tanque.setComprimentoM(geometria.comprimentoM());
        tanque.setRelacaoComprimentoLargura(geometria.relacaoComprimentoLargura());
        tanque.setDiametroM(geometria.diametroM());
        tanque.setAlturaConstrutivaM(geometria.alturaConstrutivaM());
        tanque.setVolumeRealM3(geometria.volumeRealM3());
    }

    /**
     * T — período de detenção da Tabela 2 da NBR 7229, em dias, conforme a contribuição diária
     * total. Quanto maior a vazão, menor o tempo que o efluente permanece no tanque.
     */
    private double periodoDetencaoDias(int contribuicaoDiariaLitros) {
        if (contribuicaoDiariaLitros <= 1500) return 1.00;
        if (contribuicaoDiariaLitros <= 3000) return 0.92;
        if (contribuicaoDiariaLitros <= 4500) return 0.83;
        if (contribuicaoDiariaLitros <= 6000) return 0.75;
        if (contribuicaoDiariaLitros <= 7500) return 0.67;
        if (contribuicaoDiariaLitros <= 9000) return 0.58;
        return 0.50;
    }

    private static double arredondar(double valor, int casas) {
        double fator = Math.pow(10, casas);
        return Math.round(valor * fator) / fator;
    }

    private TanqueSepticoEntity buscarEntidade(Long id) {
        return tanqueSepticoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de tanque séptico não encontrado"));
    }

    private TanqueSepticoResponseDTO toDTO(TanqueSepticoEntity t) {
        double volumeUtilM3 = t.getVolumeLitros() / (double) LITROS_POR_M3;

        // Cálculos gravados antes da geometria existir não têm dimensões para exibir.
        GeometriaTanqueSeptico.Faixa faixa = t.getProfundidadeUtilM() != null
                ? GeometriaTanqueSeptico.Faixa.para(volumeUtilM3)
                : null;

        Double folgaM3 = t.getVolumeRealM3() != null
                ? arredondar(t.getVolumeRealM3() - volumeUtilM3, 3)
                : null;
        Double folgaPercentual = folgaM3 != null
                ? arredondar(folgaM3 / volumeUtilM3 * 100, 1)
                : null;

        return new TanqueSepticoResponseDTO(
                t.getId(),
                t.getEmpreendimento().getId(),
                t.getTaxaOcupacao(),
                t.getNumUnidades(),
                t.getContribuicaoDespejo(),
                t.getFaixaTemperatura(),
                t.getIntervaloLimpezaAnos(),
                FORMULA,
                t.getPopulacao(),
                t.getContribuicaoDespejo().getUnidade(),
                t.getContribuicaoDespejo().getContribuicaoLitros(),
                t.getContribuicaoDespejo().getLodoFrescoLitros(),
                t.getPeriodoDetencaoDias(),
                t.getTaxaAcumulacaoDias(),
                t.getContribuicaoDiariaLitros(),
                VAZAO_MAXIMA_LITROS_DIA,
                t.getContribuicaoDiariaLitros() > VAZAO_MAXIMA_LITROS_DIA,
                t.getVolumeCalculadoLitros(),
                t.getVolumeLitros(),
                VOLUME_MINIMO_LITROS,
                t.getVolumeCalculadoLitros() < VOLUME_MINIMO_LITROS,
                t.getFormaTanque(),
                t.getProfundidadeInformadaM(),
                t.getProfundidadeUtilM(),
                faixa != null ? faixa.minimoM() : null,
                faixa != null ? faixa.maximoM() : null,
                t.getLarguraM(),
                t.getComprimentoM(),
                t.getRelacaoComprimentoLargura(),
                t.getDiametroM(),
                t.getAlturaConstrutivaM(),
                GeometriaTanqueSeptico.CAMARA_GASES_CM / 100.0,
                t.getVolumeRealM3(),
                folgaM3,
                folgaPercentual,
                t.getCriadoEm(),
                t.getAtualizadoEm());
    }
}
