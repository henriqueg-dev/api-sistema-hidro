package api.sistema.hidro.catalogo.repository;

import api.sistema.hidro.catalogo.entity.UsuarioIndiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioIndiceRepository extends JpaRepository<UsuarioIndiceEntity, String> {
}
