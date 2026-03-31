package api.sistema.hidro.service;

import api.sistema.hidro.dto.EmpreendimentoRequestDTO;
import api.sistema.hidro.dto.EmpreendimentoResponseDTO;
import api.sistema.hidro.model.EmpreendimentoModel;
import api.sistema.hidro.model.EmpresaModel;
import api.sistema.hidro.repository.EmpreendimentoRepository;
import api.sistema.hidro.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpreendimentoService {

    private final EmpreendimentoRepository empreendimentoRepository;
    private final EmpresaRepository empresaRepository;

    public EmpreendimentoResponseDTO criar(EmpreendimentoRequestDTO dto) {
        EmpresaModel empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        EmpreendimentoModel empreendimento = EmpreendimentoModel.builder()
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

    private EmpreendimentoResponseDTO toDTO(EmpreendimentoModel e) {
        return new EmpreendimentoResponseDTO(
                e.getId(), e.getNome(), e.getTipo(),
                e.getNumPavimentos(), e.getEndereco(),
                e.getConcessionaria(), e.getEmpresa().getId(),
                e.getEmpresa().getNome(), e.getAtivo(), e.getCriadoEm());
    }
}