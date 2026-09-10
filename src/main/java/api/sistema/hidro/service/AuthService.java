package api.sistema.hidro.service;

import api.sistema.hidro.dto.LoginRequest;
import api.sistema.hidro.dto.LoginResponse;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.FinalidadeCodigo;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CodigoVerificacaoService codigoVerificacaoService;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getSenha()));

        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        String token = jwtUtil.gerarToken(usuarioEntity.getEmail(), usuarioEntity.getPerfil().name());

        return new LoginResponse(token, usuarioEntity.getNome(), usuarioEntity.getEmail(),
                usuarioEntity.getPerfil().name());
    }

    @Transactional
    public void esqueciMinhaSenha(String email) {
        usuarioRepository.findByEmail(email)
                .filter(usuario -> !Boolean.TRUE.equals(usuario.getConvitePendente()))
                .ifPresent(usuario -> {
                    String codigo = codigoVerificacaoService.gerar(usuario, FinalidadeCodigo.RECUPERACAO_SENHA);
                    emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo);
                });
    }

    @Transactional
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        UsuarioEntity usuarioEntity = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        FinalidadeCodigo finalidade = codigoVerificacaoService.validar(usuarioEntity, codigo);

        usuarioEntity.setSenha(passwordEncoder.encode(novaSenha));
        if (finalidade == FinalidadeCodigo.CONVITE) {
            usuarioEntity.setAtivo(true);
            usuarioEntity.setConvitePendente(false);
        }
        usuarioRepository.save(usuarioEntity);
    }
}