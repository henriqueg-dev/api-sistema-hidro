package api.sistema.hidro.security;

import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuarioEntity = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return User.builder()
                .username(usuarioEntity.getEmail())
                .password(usuarioEntity.getSenha())
                .roles(usuarioEntity.getPerfil().name())
                .disabled(!Boolean.TRUE.equals(usuarioEntity.getAtivo()))
                .build();
    }
}