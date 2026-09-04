package api.sistema.hidro.security;

import api.sistema.hidro.entity.RevisaoEntity;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Fica nulo quando a alteração não parte de uma requisição autenticada (ex.: carga inicial). */
public class RevisaoListener implements RevisionListener {

    @Override
    public void newRevision(Object revisao) {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !(autenticacao.getPrincipal() instanceof UsuarioAutenticado usuario)) {
            return;
        }

        RevisaoEntity revisaoEntity = (RevisaoEntity) revisao;
        revisaoEntity.setUsuarioId(usuario.getId());
        revisaoEntity.setUsuarioNome(usuario.getNome());
        revisaoEntity.setUsuarioEmail(usuario.getUsername());
    }
}
