package api.sistema.hidro.service;

import api.sistema.hidro.dto.ConexaoTrechoRequestDTO;
import api.sistema.hidro.dto.ConexaoTrechoResponseDTO;
import api.sistema.hidro.dto.PiscinaReferenciasDTO;
import api.sistema.hidro.dto.PiscinaRequestDTO;
import api.sistema.hidro.dto.PiscinaResponseDTO;
import api.sistema.hidro.dto.TrechoPiscinaRequestDTO;
import api.sistema.hidro.dto.TrechoPiscinaResponseDTO;
import api.sistema.hidro.entity.ConexaoTrechoEntity;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.PiscinaEntity;
import api.sistema.hidro.entity.TrechoPiscinaEntity;
import api.sistema.hidro.enums.DiametroPiscina;
import api.sistema.hidro.enums.SentidoTrecho;
import api.sistema.hidro.enums.TipoConexao;
import api.sistema.hidro.enums.TipoUsoPiscina;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.ConexaoTrechoRepository;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.PiscinaRepository;
import api.sistema.hidro.repository.TrechoPiscinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiscinaService {

    /** Constante da fórmula de Fair-Whipple-Hsiao para PVC. */
    private static final double FWH_CONSTANTE = 8.69e6;

    /** Ajuste do memorial de origem sobre a constante, mantido por fidelidade. */
    private static final double FWH_FATOR = 0.11;

    private static final double FWH_EXPOENTE_VAZAO = 1.75;
    private static final double FWH_EXPOENTE_DIAMETRO = 4.75;

    /** Vazão atendida por bocal de retorno (m³/h). */
    private static final double VAZAO_POR_BOCAL_M3H = 5.0;

    /** Área servida por ralo de fundo (m²). */
    private static final int AREA_POR_RALO_M2 = 50;

    private static final int AREA_POR_SKIMMER_PADRAO_M2 = 50;

    /** NBR 10339: no mínimo dois ralos interligados, por segurança. */
    private static final int MIN_RALOS = 2;

    /** Sem ralo de fundo, a sucção só por skimmer exige no mínimo dois. */
    private static final int MIN_SKIMMERS_SEM_RALO = 2;

    private static final int MIN_BOCAIS_RETORNO = 2;

    private final PiscinaRepository piscinaRepository;
    private final TrechoPiscinaRepository trechoRepository;
    private final ConexaoTrechoRepository conexaoRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public PiscinaResponseDTO criar(PiscinaRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        PiscinaEntity piscina = PiscinaEntity.builder().empreendimento(empreendimento).build();

        aplicarCalculo(piscina, dto);
        piscinaRepository.save(piscina);
        salvarTrechos(piscina, dto.getTrechos());

        return montarResposta(piscina);
    }

    @Transactional
    public PiscinaResponseDTO atualizar(Long id, PiscinaRequestDTO dto) {
        PiscinaEntity piscina = buscarEntidade(id);

        aplicarCalculo(piscina, dto);
        piscinaRepository.save(piscina);

        apagarTrechos(piscina.getId());
        salvarTrechos(piscina, dto.getTrechos());

        return montarResposta(piscina);
    }

    @Transactional
    public void excluir(Long id) {
        PiscinaEntity piscina = buscarEntidade(id);
        apagarTrechos(piscina.getId());
        piscinaRepository.delete(piscina);
    }

    public List<PiscinaResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return piscinaRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::montarResposta)
                .toList();
    }

    public PiscinaResponseDTO buscarPorId(Long id) {
        return montarResposta(buscarEntidade(id));
    }

    // ------------------------------------------------------------------
    // Dimensionamento da piscina
    // ------------------------------------------------------------------

    private void aplicarCalculo(PiscinaEntity piscina, PiscinaRequestDTO dto) {
        int areaPorSkimmer = dto.getAreaPorSkimmerM2() != null
                ? dto.getAreaPorSkimmerM2()
                : AREA_POR_SKIMMER_PADRAO_M2;

        double area = dto.getLarguraM() * dto.getComprimentoM();
        double volume = area * dto.getProfundidadeM();
        double vazaoProjeto = volume / dto.getTempoFiltracaoH();

        if (dto.getVazaoBombaM3h() < vazaoProjeto) {
            throw new RegraNegocioException(String.format(
                    "A vazão da bomba (%.2f m³/h) é menor que a vazão de projeto (%.2f m³/h). "
                            + "Escolha uma bomba de maior vazão ou aumente o tempo de filtração.",
                    dto.getVazaoBombaM3h(), vazaoProjeto));
        }

        DiametroPiscina recalque = DiametroPiscina.paraRecalque(dto.getVazaoBombaM3h());
        DiametroPiscina succao = DiametroPiscina.paraSuccao(dto.getVazaoBombaM3h());

        piscina.setNome(dto.getNome());
        piscina.setTipoUso(dto.getTipoUso());
        piscina.setLarguraM(dto.getLarguraM());
        piscina.setComprimentoM(dto.getComprimentoM());
        piscina.setProfundidadeM(dto.getProfundidadeM());
        piscina.setTempoFiltracaoH(dto.getTempoFiltracaoH());
        piscina.setVazaoBombaM3h(dto.getVazaoBombaM3h());
        piscina.setAlturaManometricaMca(dto.getAlturaManometricaMca());
        piscina.setAreaPorSkimmerM2(areaPorSkimmer);
        piscina.setNumAspiradores(dto.getNumAspiradores());

        piscina.setAreaM2(area);
        piscina.setVolumeM3(volume);
        piscina.setVazaoProjetoM3h(vazaoProjeto);

        piscina.setDnRecalqueMm(recalque.getDn());
        piscina.setDnSuccaoMm(succao.getDn());
        piscina.setVelocidadeRecalqueMs(recalque.velocidadeMs(dto.getVazaoBombaM3h()));
        piscina.setVelocidadeSuccaoMs(succao.velocidadeMs(dto.getVazaoBombaM3h()));

        double bocaisCalculado = dto.getVazaoBombaM3h() / VAZAO_POR_BOCAL_M3H;
        double skimmersCalculado = area / areaPorSkimmer;
        double ralosCalculado = area / AREA_POR_RALO_M2;

        piscina.setNumBocaisRetornoCalculado(bocaisCalculado);
        piscina.setNumSkimmersCalculado(skimmersCalculado);
        piscina.setNumRalosCalculado(ralosCalculado);

        piscina.setNumBocaisRetornoAdotado(adotar(
                dto.getNumBocaisRetornoAdotado(), bocaisCalculado, MIN_BOCAIS_RETORNO));
        piscina.setNumSkimmersAdotado(adotar(
                dto.getNumSkimmersAdotado(), skimmersCalculado, 0));
        piscina.setNumRalosAdotado(adotar(
                dto.getNumRalosAdotado(), ralosCalculado, MIN_RALOS));
    }

    /** Arredonda para cima e respeita o mínimo, salvo quando o projetista fixa o valor. */
    private int adotar(Integer override, double calculado, int minimo) {
        if (override != null) return override;
        return Math.max(minimo, (int) Math.ceil(calculado));
    }

    // ------------------------------------------------------------------
    // Perda de carga trecho a trecho
    // ------------------------------------------------------------------

    private void salvarTrechos(PiscinaEntity piscina, List<TrechoPiscinaRequestDTO> trechos) {
        if (trechos == null || trechos.isEmpty()) {
            piscina.setPressaoResidualMca(null);
            piscinaRepository.save(piscina);
            return;
        }

        // A pressão parte da bomba e é repassada de um trecho para o seguinte.
        double pressaoMontante = piscina.getAlturaManometricaMca();
        int ordem = 1;

        for (TrechoPiscinaRequestDTO dto : trechos) {
            DiametroPiscina diametro = DiametroPiscina.porDn(dto.getDnMm());
            double lEquivalenteAdicional = dto.getLEquivalenteAdicionalM() != null
                    ? dto.getLEquivalenteAdicionalM()
                    : 0.0;

            double lEquivalente = lEquivalenteAdicional
                    + somarConexoes(dto.getConexoes(), dto.getDnMm());
            double lTotal = lEquivalente + dto.getLRealM();

            double vazaoLs = dto.getVazaoM3h() / 3.6;
            double perdaUnitaria = perdaUnitaria(vazaoLs, diametro.getDiametroInternoMm());
            double hf = perdaUnitaria * lTotal;
            double pressaoJusante = dto.getSentido()
                    .pressaoJusante(pressaoMontante, dto.getDesnivelM(), hf);

            TrechoPiscinaEntity trecho = TrechoPiscinaEntity.builder()
                    .piscina(piscina)
                    .ordem(ordem++)
                    .nome(dto.getNome())
                    .sentido(dto.getSentido())
                    .vazaoM3h(dto.getVazaoM3h())
                    .dnMm(dto.getDnMm())
                    .desnivelM(dto.getDesnivelM())
                    .lRealM(dto.getLRealM())
                    .lEquivalenteAdicionalM(lEquivalenteAdicional)
                    .diametroInternoMm(diametro.getDiametroInternoMm())
                    .vazaoLs(vazaoLs)
                    .velocidadeMs(diametro.velocidadeMs(dto.getVazaoM3h()))
                    .perdaUnitariaMM(perdaUnitaria)
                    .lEquivalenteM(lEquivalente)
                    .lTotalM(lTotal)
                    .hfM(hf)
                    .pressaoMontanteMca(pressaoMontante)
                    .pressaoJusanteMca(pressaoJusante)
                    .build();

            trechoRepository.save(trecho);
            salvarConexoes(trecho, dto.getConexoes());

            pressaoMontante = pressaoJusante;
        }

        // A pressão residual é a que sobra no bocal mais desfavorável.
        piscina.setPressaoResidualMca(pressaoMontante);
        piscinaRepository.save(piscina);
    }

    /**
     * Fair-Whipple-Hsiao para PVC: J = 8,69e6 x Q^1,75 x (1 / D^4,75) x 0,11,
     * com Q em L/s e D interno em mm.
     */
    private double perdaUnitaria(double vazaoLs, double diametroInternoMm) {
        return FWH_CONSTANTE
                * Math.pow(vazaoLs, FWH_EXPOENTE_VAZAO)
                / Math.pow(diametroInternoMm, FWH_EXPOENTE_DIAMETRO)
                * FWH_FATOR;
    }

    private double somarConexoes(List<ConexaoTrechoRequestDTO> conexoes, int dn) {
        if (conexoes == null) return 0.0;
        return conexoes.stream()
                .mapToDouble(conexao ->
                        conexao.getQuantidade() * conexao.getTipo().comprimentoEquivalenteM(dn))
                .sum();
    }

    private void salvarConexoes(TrechoPiscinaEntity trecho, List<ConexaoTrechoRequestDTO> conexoes) {
        if (conexoes == null) return;

        for (ConexaoTrechoRequestDTO dto : conexoes) {
            conexaoRepository.save(ConexaoTrechoEntity.builder()
                    .trecho(trecho)
                    .tipo(dto.getTipo())
                    .quantidade(dto.getQuantidade())
                    .comprimentoEquivalenteM(
                            dto.getQuantidade() * dto.getTipo().comprimentoEquivalenteM(trecho.getDnMm()))
                    .build());
        }
    }

    private void apagarTrechos(Long piscinaId) {
        List<Long> ids = trechoRepository.findByPiscinaIdOrderByOrdemAsc(piscinaId)
                .stream()
                .map(TrechoPiscinaEntity::getId)
                .toList();

        if (!ids.isEmpty()) {
            conexaoRepository.deleteByTrechoIdIn(ids);
        }
        trechoRepository.deleteByPiscinaId(piscinaId);
    }

    // ------------------------------------------------------------------
    // Conformidade
    // ------------------------------------------------------------------

    /** Verificações que não impedem o cálculo, mas o projetista precisa ver. */
    private List<String> montarAlertas(PiscinaEntity piscina, List<TrechoPiscinaEntity> trechos) {
        List<String> alertas = new ArrayList<>();

        int tempoMaximo = piscina.getTipoUso().tempoMaximoFiltracaoH(piscina.getProfundidadeM());
        if (piscina.getTempoFiltracaoH() > tempoMaximo) {
            alertas.add(String.format(
                    "Tempo de filtração de %d h acima do máximo de %d h da Tabela 1 da NBR 10339 "
                            + "para %s com profundidade de %.2f m.",
                    piscina.getTempoFiltracaoH(), tempoMaximo,
                    piscina.getTipoUso().getDescricao(), piscina.getProfundidadeM()));
        }

        if (piscina.getVelocidadeSuccaoMs() > DiametroPiscina.VELOCIDADE_MAX_SUCCAO) {
            alertas.add(String.format(
                    "Velocidade na sucção de %.2f m/s acima do limite de %.1f m/s (DN %d). "
                            + "Considere o diâmetro imediatamente superior.",
                    piscina.getVelocidadeSuccaoMs(), DiametroPiscina.VELOCIDADE_MAX_SUCCAO,
                    piscina.getDnSuccaoMm()));
        }

        if (piscina.getVelocidadeRecalqueMs() > DiametroPiscina.VELOCIDADE_MAX_RECALQUE) {
            alertas.add(String.format(
                    "Velocidade no recalque de %.2f m/s acima do limite de %.1f m/s (DN %d). "
                            + "Considere o diâmetro imediatamente superior.",
                    piscina.getVelocidadeRecalqueMs(), DiametroPiscina.VELOCIDADE_MAX_RECALQUE,
                    piscina.getDnRecalqueMm()));
        }

        if (piscina.getNumRalosAdotado() > 0 && piscina.getNumRalosAdotado() < MIN_RALOS) {
            alertas.add("A NBR 10339 exige no mínimo 2 ralos de fundo interligados, "
                    + "afastados pelo menos 1,5 m entre si, com grelha antiaprisionamento.");
        }

        if (piscina.getNumRalosAdotado() == 0
                && piscina.getNumSkimmersAdotado() < MIN_SKIMMERS_SEM_RALO) {
            alertas.add("Sem ralo de fundo, a sucção fica só pelos skimmers e a NBR 10339 "
                    + "exige no mínimo 2, para não haver risco de aprisionamento.");
        }

        if (piscina.getNumRalosAdotado() == 0 && piscina.getNumSkimmersAdotado() == 0) {
            alertas.add("Nenhum dispositivo de sucção adotado: a piscina precisa de "
                    + "ralo de fundo ou skimmer.");
        }

        for (TrechoPiscinaEntity trecho : trechos) {
            double limite = trecho.getSentido().getVelocidadeMaximaMs();
            if (trecho.getVelocidadeMs() > limite) {
                alertas.add(String.format(
                        "Trecho \"%s\": velocidade de %.2f m/s acima do limite de %.1f m/s "
                                + "para o conjunto de %s.",
                        trecho.getNome(), trecho.getVelocidadeMs(), limite,
                        trecho.getSentido().getDescricao().toLowerCase()));
            }
        }

        if (piscina.getPressaoResidualMca() != null && piscina.getPressaoResidualMca() <= 0) {
            alertas.add(String.format(
                    "Pressão residual de %.2f mca no bocal mais desfavorável: a bomba não vence "
                            + "a perda de carga do circuito.",
                    piscina.getPressaoResidualMca()));
        }

        return alertas;
    }

    // ------------------------------------------------------------------
    // Montagem da resposta
    // ------------------------------------------------------------------

    private PiscinaEntity buscarEntidade(Long id) {
        return piscinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Piscina não encontrada"));
    }

    private PiscinaResponseDTO montarResposta(PiscinaEntity piscina) {
        List<TrechoPiscinaEntity> trechos =
                trechoRepository.findByPiscinaIdOrderByOrdemAsc(piscina.getId());

        return new PiscinaResponseDTO(
                piscina.getId(),
                piscina.getEmpreendimento().getId(),
                piscina.getNome(),
                piscina.getTipoUso(),
                piscina.getLarguraM(),
                piscina.getComprimentoM(),
                piscina.getProfundidadeM(),
                piscina.getTempoFiltracaoH(),
                piscina.getTipoUso().tempoMaximoFiltracaoH(piscina.getProfundidadeM()),
                piscina.getVazaoBombaM3h(),
                piscina.getAlturaManometricaMca(),
                piscina.getAreaPorSkimmerM2(),
                piscina.getAreaM2(),
                piscina.getVolumeM3(),
                piscina.getVazaoProjetoM3h(),
                piscina.getDnRecalqueMm(),
                piscina.getDnSuccaoMm(),
                piscina.getVelocidadeRecalqueMs(),
                piscina.getVelocidadeSuccaoMs(),
                piscina.getNumBocaisRetornoCalculado(),
                piscina.getNumBocaisRetornoAdotado(),
                piscina.getNumSkimmersCalculado(),
                piscina.getNumSkimmersAdotado(),
                piscina.getNumRalosCalculado(),
                piscina.getNumRalosAdotado(),
                piscina.getNumAspiradores(),
                piscina.getPressaoResidualMca(),
                trechos.stream().map(this::toTrechoDTO).toList(),
                montarAlertas(piscina, trechos),
                piscina.getCriadoEm(),
                piscina.getAtualizadoEm());
    }

    private TrechoPiscinaResponseDTO toTrechoDTO(TrechoPiscinaEntity t) {
        List<ConexaoTrechoResponseDTO> conexoes =
                conexaoRepository.findByTrechoIdOrderByIdAsc(t.getId())
                        .stream()
                        .map(c -> new ConexaoTrechoResponseDTO(
                                c.getId(), c.getTipo(), c.getQuantidade(),
                                c.getComprimentoEquivalenteM()))
                        .toList();

        return new TrechoPiscinaResponseDTO(
                t.getId(),
                t.getOrdem(),
                t.getNome(),
                t.getSentido(),
                t.getVazaoM3h(),
                t.getVazaoLs(),
                t.getDnMm(),
                t.getDiametroInternoMm(),
                t.getVelocidadeMs(),
                t.getPerdaUnitariaMM(),
                t.getDesnivelM(),
                t.getLEquivalenteAdicionalM(),
                t.getLEquivalenteM(),
                t.getLRealM(),
                t.getLTotalM(),
                t.getHfM(),
                t.getPressaoMontanteMca(),
                t.getPressaoJusanteMca(),
                conexoes);
    }

    /** Tabelas de apoio para a tela montar selects e exibir as referências. */
    public PiscinaReferenciasDTO referencias() {
        List<Map<String, Object>> tempos = new ArrayList<>();
        for (TipoUsoPiscina tipo : TipoUsoPiscina.values()) {
            tempos.add(Map.of(
                    "tipo", tipo.name(),
                    "descricao", tipo.getDescricao(),
                    "ate060", tipo.tempoMaximoFiltracaoH(0.5),
                    "de060a150", tipo.tempoMaximoFiltracaoH(1.0),
                    "acima150", tipo.tempoMaximoFiltracaoH(2.0)));
        }

        List<Map<String, Object>> diametros = new ArrayList<>();
        for (DiametroPiscina diametro : DiametroPiscina.values()) {
            diametros.add(Map.of(
                    "dn", diametro.getDn(),
                    "diametroInternoMm", diametro.getDiametroInternoMm()));
        }

        List<Map<String, Object>> conexoes = new ArrayList<>();
        for (TipoConexao tipo : TipoConexao.values()) {
            conexoes.add(Map.of(
                    "tipo", tipo.name(),
                    "descricao", tipo.getDescricao(),
                    "comprimentos", tipo.comprimentosTabelados()));
        }

        List<Integer> tabelados = new ArrayList<>();
        for (int dn : TipoConexao.diametrosTabelados()) {
            tabelados.add(dn);
        }

        return new PiscinaReferenciasDTO(
                tempos,
                diametros,
                faixas(new double[] {15, 25, 35, 53, 80}, new int[] {50, 60, 75, 85, 110}),
                faixas(new double[] {9, 15, 21, 32, 50}, new int[] {50, 60, 75, 85, 110}),
                tabelados,
                conexoes,
                DiametroPiscina.VELOCIDADE_MAX_SUCCAO,
                DiametroPiscina.VELOCIDADE_MAX_RECALQUE);
    }

    private List<Map<String, Object>> faixas(double[] limites, int[] dns) {
        List<Map<String, Object>> faixas = new ArrayList<>();
        double inicio = 0;

        for (int i = 0; i < limites.length; i++) {
            faixas.add(Map.of("de", inicio, "ate", limites[i], "dn", dns[i]));
            inicio = limites[i];
        }

        return faixas;
    }
}
