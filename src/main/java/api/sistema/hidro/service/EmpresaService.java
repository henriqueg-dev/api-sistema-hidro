package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpresaRequestDTO;
import api.sistema.hidro.dto.EmpresaResponseDTO;
import api.sistema.hidro.model.EmpresaModel;
import api.sistema.hidro.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaResponseDTO criar(EmpresaRequestDTO dto) {
        EmpresaModel empresa = EmpresaModel.builder()
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
        EmpresaModel empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
        return toDTO(empresa);
    }

    private EmpresaResponseDTO toDTO(EmpresaModel e) {
        return new EmpresaResponseDTO(e.getId(), e.getNome(), e.getAtivo(), e.getCriadoEm());
    }
}