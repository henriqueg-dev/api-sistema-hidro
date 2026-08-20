package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.EmpreendimentoEntity;
import api.sistema.hidro.entity.EmpresaEntity;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpreendimentoService {

    private final EmpreendimentoRepository empreendimentoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional
    public EmpreendimentoResponseDTO criar(EmpreendimentoRequestDTO dto) {
        EmpresaEntity empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));

        EmpreendimentoEntity empreendimento = EmpreendimentoEntity.builder()
                .nome(dto.getNome())
                .tipo(dto.getTipo())
                .numPavimentos(dto.getNumPavimentos())
                .endereco(dto.getEndereco())
                .concessionaria(dto.getConcessionaria())
                .empresa(empresa)
                .build();

        empreendimentoRepository.save(empreendimento);
        return toDTO(empreendimento);
    }

    public List<EmpreendimentoResponseDTO> listarPorEmpresa(Long empresaId) {
        return empreendimentoRepository.findByEmpresaIdAndAtivoTrue(empresaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public EmpreendimentoResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    /** A empresa vinculada não muda: o {@code empresaId} do DTO é ignorado. */
    @Transactional
    public EmpreendimentoResponseDTO atualizar(Long id, EmpreendimentoRequestDTO dto) {
        EmpreendimentoEntity empreendimento = buscarEntidade(id);
        empreendimento.setNome(dto.getNome());
        empreendimento.setTipo(dto.getTipo());
        empreendimento.setNumPavimentos(dto.getNumPavimentos());
        empreendimento.setEndereco(dto.getEndereco());
        empreendimento.setConcessionaria(dto.getConcessionaria());
        empreendimentoRepository.save(empreendimento);
        return toDTO(empreendimento);
    }

    /** Exclusão lógica: o empreendimento deixa de aparecer nas listagens da empresa. */
    @Transactional
    public void excluir(Long id) {
        EmpreendimentoEntity empreendimento = buscarEntidade(id);
        empreendimento.setAtivo(false);
        empreendimentoRepository.save(empreendimento);
    }

    private EmpreendimentoEntity buscarEntidade(Long id) {
        return empreendimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empreendimento não encontrado"));
    }

    private EmpreendimentoResponseDTO toDTO(EmpreendimentoEntity e) {
        return new EmpreendimentoResponseDTO(
                e.getId(), e.getNome(), e.getTipo(),
                e.getNumPavimentos(), e.getEndereco(),
                e.getConcessionaria(), e.getEmpresa().getId(),
                e.getEmpresa().getNome(), e.getAtivo(), e.getCriadoEm());
    }
}
