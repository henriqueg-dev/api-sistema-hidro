package api.sistema.hidro.repository;

import api.sistema.hidro.entity.CodigoVerificacaoEntity;
import api.sistema.hidro.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodigoVerificacaoRepository extends JpaRepository<CodigoVerificacaoEntity, Long> {
    Optional<CodigoVerificacaoEntity> findByUsuario(UsuarioEntity usuario);
}
