package api.sistema.hidro.service;

import api.sistema.hidro.dto.UsuarioRequestDTO;
import api.sistema.hidro.dto.UsuarioResponseDTO;
import api.sistema.hidro.enums.PerfilUsuario;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.exception.RegraNegocioException;
import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSenhaCodificada() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNome("Engenheiro Teste");
        dto.setEmail("engenheiro@hidro.com");
        dto.setSenha("senha123");
        dto.setPerfil(PerfilUsuario.ENGENHEIRO);

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("senha-codificada");

        UsuarioResponseDTO resposta = usuarioService.criar(dto);

        assertThat(resposta.getEmail()).isEqualTo("engenheiro@hidro.com");
        assertThat(resposta.getPerfil()).isEqualTo(PerfilUsuario.ENGENHEIRO);
    }

    @Test
    void deveLancarExcecaoAoCriarUsuarioComEmailJaCadastrado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("existente@hidro.com");

        when(usuarioRepository.findByEmail("existente@hidro.com"))
                .thenReturn(Optional.of(new UsuarioEntity()));

        assertThatThrownBy(() -> usuarioService.criar(dto))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository, never()).save(any(UsuarioEntity.class));
    }

    @Test
    void deveLancarExcecaoAoAlterarStatusDeUsuarioInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.alterarStatus(1L, false))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }
}
