package api.sistema.hidro.service;

import api.sistema.hidro.dto.UsuarioRequestDTO;
import api.sistema.hidro.dto.UsuarioResponseDTO;
import api.sistema.hidro.model.UsuarioModel;
import api.sistema.hidro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        UsuarioModel usuarioModel = UsuarioModel.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .perfil(dto.getPerfil())
                .ativo(true)
                .build();

        usuarioRepository.save(usuarioModel);
        return toDTO(usuarioModel);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioResponseDTO alterarStatus(Long id, Boolean ativo) {
        UsuarioModel usuarioModel = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuarioModel.setAtivo(ativo);
        usuarioRepository.save(usuarioModel);
        return toDTO(usuarioModel);
    }

    private UsuarioResponseDTO toDTO(UsuarioModel usuarioModel) {
        return new UsuarioResponseDTO(
                usuarioModel.getId(),
                usuarioModel.getNome(),
                usuarioModel.getEmail(),
                usuarioModel.getPerfil(),
                usuarioModel.getAtivo(),
                usuarioModel.getCriadoEm());
    }
}