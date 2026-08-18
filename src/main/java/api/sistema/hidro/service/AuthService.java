package api.sistema.hidro.service;

import api.sistema.hidro.dto.LoginRequest;
import api.sistema.hidro.dto.LoginResponse;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

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
}