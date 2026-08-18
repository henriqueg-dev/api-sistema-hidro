package api.sistema.hidro.service;

import api.sistema.hidro.dto.LoginRequest;
import api.sistema.hidro.dto.LoginResponse;
import api.sistema.hidro.enums.PerfilUsuario;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.repository.UsuarioRepository;
import api.sistema.hidro.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveAutenticarERetornarTokenQuandoCredenciaisValidas() {
        LoginRequest request = LoginRequest.builder()
                .email("admin@hidro.com")
                .senha("senha123")
                .build();

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome("Administrador")
                .email("admin@hidro.com")
                .perfil(PerfilUsuario.ADMIN)
                .build();

        when(usuarioRepository.findByEmail("admin@hidro.com")).thenReturn(Optional.of(usuario));
        when(jwtUtil.gerarToken("admin@hidro.com", "ADMIN")).thenReturn("token-gerado");

        LoginResponse resposta = authService.login(request);

        assertThat(resposta.getToken()).isEqualTo("token-gerado");
        assertThat(resposta.getNome()).isEqualTo("Administrador");
        assertThat(resposta.getPerfil()).isEqualTo("ADMIN");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioAutenticadoNaoExisteMaisNaBase() {
        LoginRequest request = LoginRequest.builder()
                .email("fantasma@hidro.com")
                .senha("senha123")
                .build();

        when(usuarioRepository.findByEmail("fantasma@hidro.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }
}
