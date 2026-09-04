package api.sistema.hidro.security;

import api.sistema.hidro.entity.UsuarioEntity;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

/** Carrega id e nome além do e-mail, para a auditoria saber quem fez a alteração. */
@Getter
public class UsuarioAutenticado extends User {

    private final Long id;
    private final String nome;

    public UsuarioAutenticado(UsuarioEntity usuario) {
        super(usuario.getEmail(),
                usuario.getSenha(),
                Boolean.TRUE.equals(usuario.getAtivo()),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil().name())));
        this.id = usuario.getId();
        this.nome = usuario.getNome();
    }
}
