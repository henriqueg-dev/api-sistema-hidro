package api.sistema.hidro.service;

import api.sistema.hidro.dto.ClienteRequestDTO;
import api.sistema.hidro.dto.ClienteResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.ClienteEntity;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final EmpreendimentoRepository empreendimentoRepository;

    @Transactional
    public ClienteResponseDTO criar(ClienteRequestDTO dto) {
        ClienteEntity cliente = ClienteEntity.builder()
                .nome(dto.getNome())
                .build();
        clienteRepository.save(cliente);
        return toDTO(cliente);
    }

    public List<ClienteResponseDTO> listarTodas(String busca) {
        return clienteRepository.buscarPorNome(busca == null ? "" : busca.trim())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        ClienteEntity cliente = buscarEntidade(id);
        cliente.setNome(dto.getNome());
        clienteRepository.save(cliente);
        return toDTO(cliente);
    }

    /** Exclusão lógica: a cliente e todos os seus empreendimentos deixam de ficar ativos. */
    @Transactional
    public void excluir(Long id) {
        ClienteEntity cliente = buscarEntidade(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);

        List<EmpreendimentoEntity> empreendimentos =
                empreendimentoRepository.findByClienteIdAndAtivoTrue(id);
        empreendimentos.forEach(empreendimento -> empreendimento.setAtivo(false));
        empreendimentoRepository.saveAll(empreendimentos);
    }

    private ClienteEntity buscarEntidade(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado"));
    }

    private ClienteResponseDTO toDTO(ClienteEntity e) {
        return new ClienteResponseDTO(e.getId(), e.getNome(), e.getAtivo(), e.getCriadoEm());
    }
}
