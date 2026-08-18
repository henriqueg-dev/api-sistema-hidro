package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpresaRequestDTO;
import api.sistema.hidro.dto.EmpresaResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.EmpresaEntity;
import api.sistema.hidro.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional
    public EmpresaResponseDTO criar(EmpresaRequestDTO dto) {
        EmpresaEntity empresa = EmpresaEntity.builder()
                .nome(dto.getNome())
                .build();
        empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    public List<EmpresaResponseDTO> listarTodas() {
        return empresaRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public EmpresaResponseDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    @Transactional
    public EmpresaResponseDTO atualizar(Long id, EmpresaRequestDTO dto) {
        EmpresaEntity empresa = buscarEntidade(id);
        empresa.setNome(dto.getNome());
        empresaRepository.save(empresa);
        return toDTO(empresa);
    }

    private EmpresaEntity buscarEntidade(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Empresa não encontrada"));
    }

    private EmpresaResponseDTO toDTO(EmpresaEntity e) {
        return new EmpresaResponseDTO(e.getId(), e.getNome(), e.getAtivo(), e.getCriadoEm());
    }
}
