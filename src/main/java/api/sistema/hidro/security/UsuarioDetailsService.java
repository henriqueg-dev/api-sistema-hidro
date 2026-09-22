package api.sistema.hidro.security;

import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Só é chamado depois que o tenant já foi resolvido e {@link TenantContext} está setado — pelo
 * login (que resolveu via {@code UsuarioIndiceRepository} no catálogo) ou pelo {@code JwtFiltro}
 * (que decodificou o claim do token).
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UsuarioEntity usuarioEntity = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        return new UsuarioAutenticado(usuarioEntity, TenantContext.atual());
    }
}
