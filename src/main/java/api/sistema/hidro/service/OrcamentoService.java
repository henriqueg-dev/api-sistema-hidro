package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.dto.OrcamentoRequestDTO;
import api.sistema.hidro.dto.OrcamentoResponseDTO;
import api.sistema.hidro.entity.ClienteEntity;
import api.sistema.hidro.entity.OrcamentoEntity;
import api.sistema.hidro.enums.StatusOrcamento;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.repository.ClienteRepository;
import api.sistema.hidro.repository.OrcamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrcamentoService {

    private static final int VALIDADE_PADRAO_DIAS = 30;

    private final OrcamentoRepository orcamentoRepository;
    private final ClienteRepository clienteRepository;
    private final EmpreendimentoService empreendimentoService;

    @Transactional
    public OrcamentoResponseDTO criar(OrcamentoRequestDTO dto) {
        ClienteEntity cliente = buscarCliente(dto.getClienteId());

        OrcamentoEntity orcamento = OrcamentoEntity.builder()
                .cliente(cliente)
                .build();

        aplicarDados(orcamento, dto);
        converterSeAprovado(orcamento, dto);
        orcamentoRepository.save(orcamento);
        return toDTO(orcamento);
    }

    @Transactional
    public OrcamentoResponseDTO atualizar(Long id, OrcamentoRequestDTO dto) {
        OrcamentoEntity orcamento = buscarEntidade(id);

        aplicarDados(orcamento, dto);
        converterSeAprovado(orcamento, dto);
        orcamentoRepository.save(orcamento);
        return toDTO(orcamento);
    }

    @Transactional
    public void excluir(Long id) {
        orcamentoRepository.delete(buscarEntidade(id));
    }

    public OrcamentoResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public List<OrcamentoResponseDTO> listarTodos() {
        return orcamentoRepository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private void aplicarDados(OrcamentoEntity orcamento, OrcamentoRequestDTO dto) {
        orcamento.setNomeEmpreendimento(dto.getNomeEmpreendimento());
        orcamento.setTipoEmpreendimento(dto.getTipoEmpreendimento());
        orcamento.setQuantidade(dto.getQuantidade());
        orcamento.setValorUnitario(dto.getValorUnitario());
        orcamento.setValorTotal(dto.getQuantidade() * dto.getValorUnitario());
        orcamento.setStatus(dto.getStatus());
        orcamento.setObservacoes(dto.getObservacoes());
        orcamento.setValidadeDias(dto.getValidadeDias() != null ? dto.getValidadeDias() : VALIDADE_PADRAO_DIAS);
    }

    private void converterSeAprovado(OrcamentoEntity orcamento, OrcamentoRequestDTO dto) {
        if (orcamento.getStatus() != StatusOrcamento.APROVADO || orcamento.getEmpreendimentoGeradoId() != null) {
            return;
        }

        if (dto.getNumPavimentos() == null || dto.getEndereco() == null || dto.getEndereco().isBlank()
                || dto.getConcessionaria() == null || dto.getConcessionaria().isBlank()) {
            throw new RegraNegocioException(
                    "Pra aprovar, informe pavimentos, endereço e concessionária do empreendimento.");
        }

        EmpreendimentoRequestDTO empreendimentoDTO = new EmpreendimentoRequestDTO();
        empreendimentoDTO.setNome(orcamento.getNomeEmpreendimento());
        empreendimentoDTO.setTipo(orcamento.getTipoEmpreendimento());
        empreendimentoDTO.setNumPavimentos(dto.getNumPavimentos());
        empreendimentoDTO.setEndereco(dto.getEndereco());
        empreendimentoDTO.setConcessionaria(dto.getConcessionaria());
        empreendimentoDTO.setClienteId(orcamento.getCliente().getId());

        EmpreendimentoResponseDTO criado = empreendimentoService.criar(empreendimentoDTO);
        orcamento.setEmpreendimentoGeradoId(criado.getId());
    }

    private ClienteEntity buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    private OrcamentoEntity buscarEntidade(Long id) {
        return orcamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Orçamento não encontrado"));
    }

    private OrcamentoResponseDTO toDTO(OrcamentoEntity o) {
        return new OrcamentoResponseDTO(
                o.getId(),
                o.getCliente().getId(),
                o.getCliente().getNome(),
                o.getNomeEmpreendimento(),
                o.getTipoEmpreendimento(),
                o.getQuantidade(),
                o.getValorUnitario(),
                o.getValorTotal(),
                o.getStatus(),
                o.getObservacoes(),
                o.getValidadeDias(),
                o.getCriadoEm().toLocalDate().plusDays(o.getValidadeDias()),
                o.getEmpreendimentoGeradoId(),
                o.getCriadoEm(),
                o.getAtualizadoEm());
    }
}
