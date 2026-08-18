package api.sistema.hidro.service;

import api.sistema.hidro.dto.UsuarioRequestDTO;
import api.sistema.hidro.dto.UsuarioResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.PerfilUsuario;
import api.sistema.hidro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("Email já cadastrado");
        }

        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(dto.getSenha()))
                .perfil(dto.getPerfil())
                .ativo(true)
                .build();

        usuarioRepository.save(usuarioEntity);
        return toDTO(usuarioEntity);
    }

    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO alterarStatus(Long id, Boolean ativo) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (Boolean.FALSE.equals(ativo)) {
            validarDesativacao(usuarioEntity);
        }

        usuarioEntity.setAtivo(ativo);
        usuarioRepository.save(usuarioEntity);
        return toDTO(usuarioEntity);
    }

    private void validarDesativacao(UsuarioEntity usuarioEntity) {
        if (!Boolean.TRUE.equals(usuarioEntity.getAtivo())) {
            return;
        }

        if (usuarioEntity.getEmail().equals(emailAutenticado())) {
            throw new RegraNegocioException("Não é possível desativar o próprio usuário");
        }

        if (usuarioEntity.getPerfil() == PerfilUsuario.ADMIN
                && usuarioRepository.countByPerfilAndAtivoTrue(PerfilUsuario.ADMIN) <= 1) {
            throw new RegraNegocioException(
                    "O sistema precisa ter pelo menos um administrador ativo");
        }
    }

    private String emailAutenticado() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return autenticacao != null ? autenticacao.getName() : null;
    }

    private UsuarioResponseDTO toDTO(UsuarioEntity usuarioEntity) {
        return new UsuarioResponseDTO(
                usuarioEntity.getId(),
                usuarioEntity.getNome(),
                usuarioEntity.getEmail(),
                usuarioEntity.getPerfil(),
                usuarioEntity.getAtivo(),
                usuarioEntity.getCriadoEm());
    }
}