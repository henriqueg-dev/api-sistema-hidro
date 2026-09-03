package api.sistema.hidro.service;

import api.sistema.hidro.dto.RamalPredialRequestDTO;
import api.sistema.hidro.dto.RamalPredialResponseDTO;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.RamalPredialEntity;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.RamalPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RamalPredialService {

    public static final String FORMULA = "Q = Cd / T   e   D = raiz(4Q / pi.v)";

    /** Cada empreendimento tem um único cálculo de ramal predial. */
    public static final int MAX_POR_EMPREENDIMENTO = 1;

    /** Faixa usual de velocidade no alimentador adotada por concessionárias, em m/s. */
    private static final double VELOCIDADE_CONFORTAVEL_MIN = 0.6;
    private static final double VELOCIDADE_CONFORTAVEL_MAX = 1.0;

    private final RamalPredialRepository ramalPredialRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public RamalPredialResponseDTO criar(RamalPredialRequestDTO dto) {
        EmpreendimentoEntity empreendimento = buscarEmpreendimento(dto.getEmpreendimentoId());

        if (ramalPredialRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui um cálculo de ramal predial. Altere o cálculo existente.");
        }

        RamalPredialEntity ramal = RamalPredialEntity.builder()
                .empreendimento(empreendimento)
                .build();

        aplicarCalculo(ramal, dto, empreendimento);
        ramalPredialRepository.save(ramal);
        return toDTO(ramal);
    }

    @Transactional
    public RamalPredialResponseDTO atualizar(Long id, RamalPredialRequestDTO dto) {
        RamalPredialEntity ramal = buscarEntidade(id);

        aplicarCalculo(ramal, dto, ramal.getEmpreendimento());
        ramalPredialRepository.save(ramal);
        return toDTO(ramal);
    }

    @Transactional
    public void excluir(Long id) {
        ramalPredialRepository.delete(buscarEntidade(id));
    }

    public List<RamalPredialResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return ramalPredialRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RamalPredialResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    private void aplicarCalculo(RamalPredialEntity ramal, RamalPredialRequestDTO dto,
                                EmpreendimentoEntity empreendimento) {
        CalculoRamalPredial.Resultado calculo;

        try {
            calculo = CalculoRamalPredial.dimensionar(
                    empreendimento.getTipo(),
                    dto.getTaxaOcupacao(),
                    dto.getNumUnidades(),
                    dto.getConsumoPerCapita(),
                    dto.getTempoReposicaoH(),
                    dto.getVelocidadeMaximaMs(),
                    dto.getHidrometroInformado());
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(String.format(
                    "A NBR 5626 admite reposição do consumo diário em até %d h para este tipo de "
                            + "empreendimento. Reduza o tempo informado.",
                    CalculoRamalPredial.tempoReposicaoMaximo(empreendimento.getTipo())));
        }

        ramal.setTaxaOcupacao(dto.getTaxaOcupacao());
        ramal.setNumUnidades(dto.getNumUnidades());
        ramal.setConsumoPerCapita(dto.getConsumoPerCapita());
        ramal.setTempoReposicaoH(calculo.tempoReposicaoH());
        ramal.setVelocidadeMaximaMs(calculo.velocidadeMaximaMs());
        ramal.setHidrometroInformado(dto.getHidrometroInformado());

        ramal.setPopulacao(calculo.populacao());
        ramal.setConsumoDiarioM3(calculo.consumoDiarioM3());
        ramal.setConsumoMensalM3(calculo.consumoMensalM3());
        ramal.setVazaoProjetoM3h(calculo.vazaoProjetoM3h());
        ramal.setVazaoProjetoLs(calculo.vazaoProjetoLs());
        ramal.setDiametroTeoricoMm(calculo.diametroTeoricoMm());
        ramal.setDnAdotadoMm(calculo.diametroAdotado().getDn());
        ramal.setDiametroInternoMm(calculo.diametroAdotado().getDiametroInternoMm());
        ramal.setVelocidadeMs(calculo.velocidadeMs());
        ramal.setHidrometro(calculo.hidrometro());
    }

    private List<String> montarAlertas(RamalPredialEntity ramal) {
        List<String> alertas = new ArrayList<>();

        alertas.add(String.format(
                "Hidrômetro sugerido pela vazão de projeto. O consumo previsto é de %.1f m³/mês — "
                        + "confirme na tabela da concessionária%s, que costuma dimensionar por faixa "
                        + "de consumo mensal e pode indicar medidor maior.",
                ramal.getConsumoMensalM3(),
                ramal.getEmpreendimento().getConcessionaria() != null
                        ? " " + ramal.getEmpreendimento().getConcessionaria()
                        : ""));

        if (ramal.getVelocidadeMs() > VELOCIDADE_CONFORTAVEL_MAX) {
            alertas.add(String.format(
                    "Velocidade de %.2f m/s no alimentador. Está dentro do limite de %.1f m/s, mas "
                            + "acima da faixa de %.1f a %.1f m/s que muitas concessionárias pedem para "
                            + "reduzir ruído e perda de carga — considere o diâmetro seguinte.",
                    ramal.getVelocidadeMs(), ramal.getVelocidadeMaximaMs(),
                    VELOCIDADE_CONFORTAVEL_MIN, VELOCIDADE_CONFORTAVEL_MAX));
        }

        if (ramal.getHidrometroInformado() != null) {
            alertas.add("Medidor fixado manualmente, no lugar da sugestão do cálculo.");
        }

        return alertas;
    }

    private EmpreendimentoEntity buscarEmpreendimento(Long id) {
        return empreendimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));
    }

    private RamalPredialEntity buscarEntidade(Long id) {
        return ramalPredialRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de ramal predial não encontrado"));
    }

    private RamalPredialResponseDTO toDTO(RamalPredialEntity r) {
        return new RamalPredialResponseDTO(
                r.getId(),
                r.getEmpreendimento().getId(),
                r.getTaxaOcupacao(),
                r.getNumUnidades(),
                r.getConsumoPerCapita(),
                r.getTempoReposicaoH(),
                CalculoRamalPredial.tempoReposicaoMaximo(r.getEmpreendimento().getTipo()),
                r.getVelocidadeMaximaMs(),
                FORMULA,
                r.getPopulacao(),
                r.getConsumoDiarioM3(),
                r.getConsumoMensalM3(),
                r.getVazaoProjetoM3h(),
                r.getVazaoProjetoLs(),
                r.getDiametroTeoricoMm(),
                r.getDnAdotadoMm(),
                r.getDiametroInternoMm(),
                r.getVelocidadeMs(),
                r.getHidrometro(),
                r.getHidrometro().getVazaoNominalM3h(),
                r.getHidrometro().getVazaoMaximaM3h(),
                r.getHidrometroInformado(),
                r.getHidrometroInformado() != null,
                r.getEmpreendimento().getConcessionaria(),
                montarAlertas(r),
                r.getCriadoEm(),
                r.getAtualizadoEm());
    }
}
