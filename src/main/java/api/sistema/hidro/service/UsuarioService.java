package api.sistema.hidro.service;

import api.sistema.hidro.dto.UsuarioRequestDTO;
import api.sistema.hidro.dto.UsuarioResponseDTO;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.FinalidadeCodigo;
import api.sistema.hidro.enums.PerfilUsuario;
import api.sistema.hidro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacaoService codigoVerificacaoService;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RegraNegocioException("Email já cadastrado");
        }

        UsuarioEntity usuarioEntity = UsuarioEntity.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                .senha(passwordEncoder.encode(UUID.randomUUID().toString()))
                .perfil(dto.getPerfil())
                .ativo(false)
                .convitePendente(true)
                .build();

        usuarioRepository.save(usuarioEntity);
        enviarConvite(usuarioEntity);
        return toDTO(usuarioEntity);
    }

    @Transactional
    public void reenviarConvite(Long id) {
        UsuarioEntity usuarioEntity = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuarioEntity.getConvitePendente())) {
            throw new RegraNegocioException("Este usuário já aceitou o convite");
        }

        enviarConvite(usuarioEntity);
    }

    private void enviarConvite(UsuarioEntity usuarioEntity) {
        String codigo = codigoVerificacaoService.gerar(usuarioEntity, FinalidadeCodigo.CONVITE);
        String link = frontendUrl + "/definir-senha?email=" + usuarioEntity.getEmail() + "&codigo=" + codigo;
        emailService.enviarConvite(usuarioEntity.getEmail(), usuarioEntity.getNome(), codigo, link);
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
        } else if (Boolean.TRUE.equals(usuarioEntity.getConvitePendente())) {
            throw new RegraNegocioException(
                    "Usuário ainda não aceitou o convite; reenvie o convite em vez de ativar");
        }

        usuarioEntity.setAtivo(ativo);
        usuarioRepository.save(usuarioEntity);
        return toDTO(usuarioEntity);
    }

    @Transactional
    public void alterarSenhaPropria(String senhaAtual, String novaSenha) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(emailAutenticado())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senhaAtual, usuarioEntity.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta");
        }

        usuarioEntity.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuarioEntity);
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
                usuarioEntity.getConvitePendente(),
                usuarioEntity.getCriadoEm());
    }
}