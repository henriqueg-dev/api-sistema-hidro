package api.sistema.hidro.controller;

import api.sistema.hidro.config.SecurityConfig;
import api.sistema.hidro.security.JwtFiltro;
import api.sistema.hidro.security.JwtUtil;
import api.sistema.hidro.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import({SecurityConfig.class, JwtFiltro.class})
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void deveRetornar401QuandoRequisicaoSemToken() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar403QuandoPerfilNaoAutorizado() throws Exception {
        String token = "token-engenheiro";

        when(jwtUtil.tokenValido(token)).thenReturn(true);
        when(jwtUtil.extrairEmail(token)).thenReturn("engenheiro@hidro.com");

        UserDetails usuarioEngenheiro = User.builder()
                .username("engenheiro@hidro.com")
                .password("senha-irrelevante")
                .roles("ENGENHEIRO")
                .build();
        when(userDetailsService.loadUserByUsername("engenheiro@hidro.com")).thenReturn(usuarioEngenheiro);

        mockMvc.perform(get("/api/usuarios").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
