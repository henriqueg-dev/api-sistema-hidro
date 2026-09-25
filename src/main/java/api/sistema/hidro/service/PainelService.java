package api.sistema.hidro.service;

import api.sistema.hidro.dto.OrcamentoResponseDTO;
import api.sistema.hidro.dto.PainelDTO;
import api.sistema.hidro.enums.StatusOrcamento;
import api.sistema.hidro.repository.CaixaGorduraRepository;
import api.sistema.hidro.repository.ClienteRepository;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.PiscinaRepository;
import api.sistema.hidro.repository.RamalPredialRepository;
import api.sistema.hidro.repository.TanqueSepticoRepository;
import api.sistema.hidro.repository.VazaoPredialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/** Monta os indicadores da tela inicial; cálculos de empreendimento excluído não contam. */
@Service
@RequiredArgsConstructor
@Transactional(value = "tenantTransactionManager", readOnly = true)
public class PainelService {

    private final ClienteRepository clienteRepository;
    private final EmpreendimentoRepository empreendimentoRepository;
    private final CaixaGorduraRepository caixaGorduraRepository;
    private final VazaoPredialRepository vazaoPredialRepository;
    private final RamalPredialRepository ramalPredialRepository;
    private final TanqueSepticoRepository tanqueSepticoRepository;
    private final PiscinaRepository piscinaRepository;
    private final EmpreendimentoService empreendimentoService;
    private final OrcamentoService orcamentoService;
    private final AuditoriaService auditoriaService;

    public PainelDTO montar(boolean administrador) {
        List<PainelDTO.ContagemCalculo> calculos = List.of(
                new PainelDTO.ContagemCalculo("Caixa de gordura e sabão",
                        caixaGorduraRepository.countByEmpreendimentoAtivoTrue()),
                new PainelDTO.ContagemCalculo("Vazão predial",
                        vazaoPredialRepository.countByEmpreendimentoAtivoTrue()),
                new PainelDTO.ContagemCalculo("Ramal predial e hidrômetro",
                        ramalPredialRepository.countByEmpreendimentoAtivoTrue()),
                new PainelDTO.ContagemCalculo("Tanque séptico",
                        tanqueSepticoRepository.countByEmpreendimentoAtivoTrue()),
                new PainelDTO.ContagemCalculo("Piscina",
                        piscinaRepository.countByEmpreendimentoAtivoTrue()));

        return new PainelDTO(
                clienteRepository.countByAtivoTrue(),
                empreendimentoRepository.countByAtivoTrue(),
                calculos,
                empreendimentoService.recentes(),
                administrador ? resumoOrcamentos() : null,
                administrador ? auditoriaService.recentes() : null);
    }

    private PainelDTO.ResumoOrcamentos resumoOrcamentos() {
        List<OrcamentoResponseDTO> orcamentos = orcamentoService.listarTodos();
        LocalDate hoje = LocalDate.now();

        List<OrcamentoResponseDTO> enviados = comStatus(orcamentos, StatusOrcamento.ENVIADO);
        List<OrcamentoResponseDTO> aprovados = comStatus(orcamentos, StatusOrcamento.APROVADO);
        long vencidos = orcamentos.stream()
                .filter(o -> o.getStatus() == StatusOrcamento.RASCUNHO || o.getStatus() == StatusOrcamento.ENVIADO)
                .filter(o -> o.getDataValidade() != null && o.getDataValidade().isBefore(hoje))
                .count();

        return new PainelDTO.ResumoOrcamentos(
                enviados.size(), somar(enviados), aprovados.size(), somar(aprovados), vencidos);
    }

    private static List<OrcamentoResponseDTO> comStatus(List<OrcamentoResponseDTO> orcamentos, StatusOrcamento status) {
        return orcamentos.stream().filter(o -> o.getStatus() == status).toList();
    }

    private static double somar(List<OrcamentoResponseDTO> orcamentos) {
        return orcamentos.stream().mapToDouble(o -> o.getValorTotal() == null ? 0 : o.getValorTotal()).sum();
    }
}
