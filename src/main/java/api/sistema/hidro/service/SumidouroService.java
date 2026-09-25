package api.sistema.hidro.service;

import api.sistema.hidro.dto.SumidouroRequestDTO;
import api.sistema.hidro.dto.SumidouroResponseDTO;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.SumidouroEntity;
import api.sistema.hidro.entity.TanqueSepticoEntity;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.SumidouroRepository;
import api.sistema.hidro.repository.TanqueSepticoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class SumidouroService {

    public static final String FORMULA = "A = Cd / Tx   e   A = πD²/4 + πD·h";

    /** Cada empreendimento tem um único cálculo de sumidouro, que pode somar várias unidades. */
    public static final int MAX_POR_EMPREENDIMENTO = 1;

    private final SumidouroRepository sumidouroRepository;
    private final EmpreendimentoRepository empreendimentoRepository;
    private final TanqueSepticoRepository tanqueSepticoRepository;

    @Transactional("tenantTransactionManager")
    public SumidouroResponseDTO criar(SumidouroRequestDTO dto) {
        EmpreendimentoEntity empreendimento = empreendimentoRepository.findById(dto.getEmpreendimentoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));

        if (sumidouroRepository.countByEmpreendimentoId(empreendimento.getId()) >= MAX_POR_EMPREENDIMENTO) {
            throw new RegraNegocioException(
                    "Este empreendimento já possui um cálculo de sumidouro. Altere o cálculo existente.");
        }

        dimensionar(dto.getContribuicaoDiariaLitros(), dto.getTaxaPercolacao(), dto.getDiametro(),
                dto.getNumSumidouros());
        SumidouroEntity sumidouro = SumidouroEntity.builder().empreendimento(empreendimento).build();
        aplicarDados(sumidouro, dto);
        sumidouroRepository.save(sumidouro);
        return toDTO(sumidouro);
    }

    @Transactional("tenantTransactionManager")
    public SumidouroResponseDTO atualizar(Long id, SumidouroRequestDTO dto) {
        SumidouroEntity sumidouro = buscarEntidade(id);

        dimensionar(dto.getContribuicaoDiariaLitros(), dto.getTaxaPercolacao(), dto.getDiametro(),
                dto.getNumSumidouros());
        aplicarDados(sumidouro, dto);
        sumidouroRepository.save(sumidouro);
        return toDTO(sumidouro);
    }

    @Transactional("tenantTransactionManager")
    public void excluir(Long id) {
        sumidouroRepository.delete(buscarEntidade(id));
    }

    public List<SumidouroResponseDTO> listarPorEmpreendimento(Long empreendimentoId) {
        return sumidouroRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(empreendimentoId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public SumidouroResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    private CalculoSumidouro.Resultado dimensionar(int contribuicao, double percolacao, double diametro,
                                                   int numSumidouros) {
        try {
            return CalculoSumidouro.dimensionar(contribuicao, percolacao, diametro, numSumidouros);
        } catch (IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }
    }

    private void aplicarDados(SumidouroEntity sumidouro, SumidouroRequestDTO dto) {
        sumidouro.setContribuicaoDiariaLitros(dto.getContribuicaoDiariaLitros());
        sumidouro.setTaxaPercolacao(dto.getTaxaPercolacao());
        sumidouro.setDiametro(dto.getDiametro());
        sumidouro.setNumSumidouros(dto.getNumSumidouros());
    }

    private SumidouroEntity buscarEntidade(Long id) {
        return sumidouroRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cálculo de sumidouro não encontrado"));
    }

    private List<String> montarAlertas(SumidouroEntity s, CalculoSumidouro.Resultado c) {
        List<String> alertas = new ArrayList<>();

        if (s.getDiametro() < CalculoSumidouro.DIAMETRO_MINIMO_M) {
            alertas.add(String.format("Diâmetro de %.2f m abaixo do mínimo de %.2f m pedido pela NBR 17076:2024.",
                    s.getDiametro(), CalculoSumidouro.DIAMETRO_MINIMO_M));
        }
        if (c.alturaTeoricaM() <= 0) {
            alertas.add("O fundo sozinho já oferece a área necessária: reduza o diâmetro ou confira a contribuição.");
        }

        // O efluente que infiltra é o que sai do tanque séptico; valores diferentes indicam dado desatualizado.
        tanqueSepticoRepository.findByEmpreendimentoIdOrderByCriadoEmAsc(s.getEmpreendimento().getId())
                .stream()
                .findFirst()
                .map(TanqueSepticoEntity::getContribuicaoDiariaLitros)
                .filter(contribuicaoTanque -> !Objects.equals(contribuicaoTanque, s.getContribuicaoDiariaLitros()))
                .ifPresent(contribuicaoTanque -> alertas.add(String.format(
                        "A contribuição informada (%d L/dia) difere da do tanque séptico deste "
                                + "empreendimento (%d L/dia).",
                        s.getContribuicaoDiariaLitros(), contribuicaoTanque)));

        return alertas;
    }

    private SumidouroResponseDTO toDTO(SumidouroEntity s) {
        CalculoSumidouro.Resultado c = dimensionar(s.getContribuicaoDiariaLitros(), s.getTaxaPercolacao(),
                s.getDiametro(), s.getNumSumidouros());

        return SumidouroResponseDTO.builder()
                .id(s.getId())
                .empreendimentoId(s.getEmpreendimento().getId())
                .contribuicaoDiariaLitros(s.getContribuicaoDiariaLitros())
                .taxaPercolacao(s.getTaxaPercolacao())
                .diametro(s.getDiametro())
                .numSumidouros(s.getNumSumidouros())
                .formula(FORMULA)
                .taxaAplicacao(c.taxaAplicacao())
                .contribuicaoM3Dia(c.contribuicaoM3Dia())
                .areaTotalM2(c.areaTotalM2())
                .areaPorSumidouroM2(c.areaPorSumidouroM2())
                .areaFundoM2(c.areaFundoM2())
                .alturaTeoricaM(c.alturaTeoricaM())
                .alturaUtilM(c.alturaUtilM())
                .areaRealM2(c.areaRealM2())
                .volumeUtilM3(c.volumeUtilM3())
                .alertas(montarAlertas(s, c))
                .criadoEm(s.getCriadoEm())
                .atualizadoEm(s.getAtualizadoEm())
                .build();
    }
}
