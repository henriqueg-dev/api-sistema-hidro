package api.sistema.hidro.repository;

import api.sistema.hidro.entity.UsuarioEntity;
import api.sistema.hidro.enums.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    Optional<UsuarioEntity> findByEmail(String email);

    long countByPerfilAndAtivoTrue(PerfilUsuario perfil);
}