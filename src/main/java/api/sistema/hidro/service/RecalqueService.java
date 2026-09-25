package api.sistema.hidro.service;

import api.sistema.hidro.dto.ConexaoTrechoRequestDTO;
import api.sistema.hidro.dto.ConexaoTrechoResponseDTO;
import api.sistema.hidro.dto.RecalqueRequestDTO;
import api.sistema.hidro.dto.RecalqueResponseDTO;
import api.sistema.hidro.entity.ConexaoRecalqueEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.RecalqueEntity;
import api.sistema.hidro.enums.TrechoRecalque;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.ConexaoRecalqueRepository;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.RecalqueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class RecalqueService {

    /** Cada empreendimento tem um único cálculo de recalque. */
    public static final int MAX_POR_EMPREENDIMENTO = 1;

    private final RecalqueRepository recalqueRepository;
    private final ConexaoRecalqueRepository conexaoRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional("tenantTransactionManager")
    public RecalqueResponseDTO criar(RecalqueRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        if (recalqueRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui um cálculo de recalque. Altere o cálculo existente.");
        }

        validar(dto);
        RecalqueEntity recalque = RecalqueEntity.builder().empreendimento(empreendimento).build();
        aplicarDados(recalque, dto);
        recalqueRepository.save(recalque);
        salvarConexoes(recalque, dto);
        return toDTO(recalque);
    }

    @Transactional("tenantTransactionManager")
    public RecalqueResponseDTO atualizar(Long id, RecalqueRequestDTO dto) {
        RecalqueEntity recalque = buscarEntidade(id);

        validar(dto);
        aplicarDados(recalque, dto);
        recalqueRepository.save(recalque);
        conexaoRepository.deleteByRecalqueId(id);
        salvarConexoes(recalque, dto);
        return toDTO(recalque);
    }

    @Transactional("tenantTransactionManager")
    public void excluir(Long id) {
        RecalqueEntity recalque = buscarEntidade(id);
        conexaoRepository.deleteByRecalqueId(id);
        recalqueRepository.delete(recalque);
    }

    public List<RecalqueResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return recalqueRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RecalqueResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /** Dimensiona antes de gravar: dado que não fecha o cálculo vira erro de regra, não 500. */
    private void validar(RecalqueRequestDTO dto) {
        dimensionar(dto.getTaxaOcupacao(), dto.getNumUnidades(), dto.getConsumoPerCapita(),
                dto.getHorasFuncionamento(), dto.getDesnivelSuccao(), dto.getComprimentoSuccao(),
                paraCalculo(dto.getConexoesSuccao()), dto.getDesnivelRecalque(),
                dto.getComprimentoRecalque(), paraCalculo(dto.getConexoesRecalque()),
                dto.getRendimentoPercentual());
    }

    private CalculoRecalque.Resultado dimensionar(int taxaOcupacao, int numUnidades, int consumoPerCapita,
                                                  double horas, double desnivelSuccao,
                                                  double comprimentoSuccao,
                                                  List<CalculoRecalque.Conexao> conexoesSuccao,
                                                  double desnivelRecalque, double comprimentoRecalque,
                                                  List<CalculoRecalque.Conexao> conexoesRecalque,
                                                  int rendimento) {
        try {
            return CalculoRecalque.dimensionar(taxaOcupacao, numUnidades, consumoPerCapita, horas,
                    desnivelSuccao, comprimentoSuccao, conexoesSuccao,
                    desnivelRecalque, comprimentoRecalque, conexoesRecalque, rendimento);
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }

    private List<CalculoRecalque.Conexao> paraCalculo(List<ConexaoTrechoRequestDTO> conexoes) {
        if (conexoes == null) return List.of();
        return conexoes.stream()
                .map(c -> new CalculoRecalque.Conexao(c.getTipo(), c.getQuantidade()))
                .toList();
    }

    private void aplicarDados(RecalqueEntity recalque, RecalqueRequestDTO dto) {
        recalque.setTaxaOcupacao(dto.getTaxaOcupacao());
        recalque.setNumUnidades(dto.getNumUnidades());
        recalque.setConsumoPerCapita(dto.getConsumoPerCapita());
        recalque.setHorasFuncionamento(dto.getHorasFuncionamento());
        recalque.setDesnivelSuccao(dto.getDesnivelSuccao());
        recalque.setComprimentoSuccao(dto.getComprimentoSuccao());
        recalque.setDesnivelRecalque(dto.getDesnivelRecalque());
        recalque.setComprimentoRecalque(dto.getComprimentoRecalque());
        recalque.setRendimentoPercentual(dto.getRendimentoPercentual());
    }

    private void salvarConexoes(RecalqueEntity recalque, RecalqueRequestDTO dto) {
        salvarConexoes(recalque, TrechoRecalque.SUCCAO, dto.getConexoesSuccao());
        salvarConexoes(recalque, TrechoRecalque.RECALQUE, dto.getConexoesRecalque());
    }

    private void salvarConexoes(RecalqueEntity recalque, TrechoRecalque trecho,
                                List<ConexaoTrechoRequestDTO> conexoes) {
        if (conexoes == null) return;
        for (ConexaoTrechoRequestDTO conexao : conexoes) {
            conexaoRepository.save(ConexaoRecalqueEntity.builder()
                    .recalque(recalque)
                    .trecho(trecho)
                    .tipo(conexao.getTipo())
                    .quantidade(conexao.getQuantidade())
                    .build());
        }
    }

    private RecalqueEntity buscarEntidade(Long id) {
        return recalqueRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de recalque não encontrado"));
    }

    private List<ConexaoRecalqueEntity> conexoesDo(Long recalqueId, TrechoRecalque trecho) {
        return conexaoRepository.findByRecalqueIdOrderByIdAsc(recalqueId)
                .stream()
                .filter(c -> c.getTrecho() == trecho)
                .toList();
    }

    private List<CalculoRecalque.Conexao> paraCalculoDe(List<ConexaoRecalqueEntity> conexoes) {
        return conexoes.stream().map(c -> new CalculoRecalque.Conexao(c.getTipo(), c.getQuantidade())).toList();
    }

    private List<ConexaoTrechoResponseDTO> paraResposta(List<ConexaoRecalqueEntity> conexoes, int dn) {
        return conexoes.stream()
                .map(c -> new ConexaoTrechoResponseDTO(c.getId(), c.getTipo(), c.getQuantidade(),
                        c.getQuantidade() * c.getTipo().comprimentoEquivalenteM(dn)))
                .toList();
    }

    private List<String> montarAlertas(RecalqueEntity r, CalculoRecalque.Resultado c) {
        List<String> alertas = new ArrayList<>();

        if (r.getHorasFuncionamento() > c.horasMaximas()) {
            alertas.add(String.format(
                    "Com %.1f h de funcionamento por dia, a bomba repõe %.1f%% do consumo diário por "
                            + "hora, abaixo dos 15%% usuais. Reduza para até %.2f h.",
                    r.getHorasFuncionamento(), 100 / r.getHorasFuncionamento(), c.horasMaximas()));
        }
        if (c.recalque().velocidadeMs() > CalculoRecalque.VELOCIDADE_MAXIMA_MS) {
            alertas.add(String.format("Velocidade de %.2f m/s no recalque passa dos %.1f m/s da NBR 5626.",
                    c.recalque().velocidadeMs(), CalculoRecalque.VELOCIDADE_MAXIMA_MS));
        }
        if (c.succao().velocidadeMs() > CalculoRecalque.VELOCIDADE_MAXIMA_MS) {
            alertas.add(String.format("Velocidade de %.2f m/s na sucção passa dos %.1f m/s da NBR 5626.",
                    c.succao().velocidadeMs(), CalculoRecalque.VELOCIDADE_MAXIMA_MS));
        }
        if (c.motorComercialCv() == null) {
            alertas.add(String.format(
                    "A potência com folga (%.2f cv) passa do maior motor da lista; escolha no catálogo do fabricante.",
                    c.potenciaComFolgaCv()));
        }
        return alertas;
    }

    private RecalqueResponseDTO toDTO(RecalqueEntity r) {
        List<ConexaoRecalqueEntity> succao = conexoesDo(r.getId(), TrechoRecalque.SUCCAO);
        List<ConexaoRecalqueEntity> recalque = conexoesDo(r.getId(), TrechoRecalque.RECALQUE);

        CalculoRecalque.Resultado c = dimensionar(r.getTaxaOcupacao(), r.getNumUnidades(),
                r.getConsumoPerCapita(), r.getHorasFuncionamento(), r.getDesnivelSuccao(),
                r.getComprimentoSuccao(), paraCalculoDe(succao), r.getDesnivelRecalque(),
                r.getComprimentoRecalque(), paraCalculoDe(recalque), r.getRendimentoPercentual());

        return RecalqueResponseDTO.builder()
                .id(r.getId())
                .empreendimentoId(r.getEmpreendimento().getId())
                .taxaOcupacao(r.getTaxaOcupacao())
                .numUnidades(r.getNumUnidades())
                .consumoPerCapita(r.getConsumoPerCapita())
                .horasFuncionamento(r.getHorasFuncionamento())
                .desnivelSuccao(r.getDesnivelSuccao())
                .comprimentoSuccao(r.getComprimentoSuccao())
                .conexoesSuccao(paraResposta(succao, c.succao().diametro().getDn()))
                .desnivelRecalque(r.getDesnivelRecalque())
                .comprimentoRecalque(r.getComprimentoRecalque())
                .conexoesRecalque(paraResposta(recalque, c.recalque().diametro().getDn()))
                .rendimentoPercentual(r.getRendimentoPercentual())
                .populacao(c.populacao())
                .consumoDiarioLitros(c.consumoDiarioLitros())
                .vazaoLs(c.vazaoLs())
                .vazaoM3h(c.vazaoM3h())
                .horasMaximas(c.horasMaximas())
                .diametroTeoricoMm(c.diametroTeoricoMm())
                .dnRecalqueMm(c.recalque().diametro().getDn())
                .internoRecalqueMm(c.recalque().diametro().getDiametroInternoMm())
                .velocidadeRecalqueMs(c.recalque().velocidadeMs())
                .perdaUnitariaRecalque(c.recalque().perdaUnitaria())
                .comprimentoEquivalenteRecalqueM(c.recalque().comprimentoEquivalenteM())
                .perdaCargaRecalqueM(c.recalque().perdaCargaM())
                .dnSuccaoMm(c.succao().diametro().getDn())
                .internoSuccaoMm(c.succao().diametro().getDiametroInternoMm())
                .velocidadeSuccaoMs(c.succao().velocidadeMs())
                .perdaUnitariaSuccao(c.succao().perdaUnitaria())
                .comprimentoEquivalenteSuccaoM(c.succao().comprimentoEquivalenteM())
                .perdaCargaSuccaoM(c.succao().perdaCargaM())
                .cargaVelocidadeM(c.cargaVelocidadeM())
                .alturaManometricaM(c.alturaManometricaM())
                .potenciaCv(c.potenciaCv())
                .potenciaKw(c.potenciaKw())
                .folgaPercentual(c.folgaPercentual())
                .potenciaComFolgaCv(c.potenciaComFolgaCv())
                .motorComercialCv(c.motorComercialCv())
                .alertas(montarAlertas(r, c))
                .criadoEm(r.getCriadoEm())
                .atualizadoEm(r.getAtualizadoEm())
                .build();
    }
}
