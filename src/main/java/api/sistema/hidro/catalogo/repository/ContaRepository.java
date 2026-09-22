package api.sistema.hidro.catalogo.repository;

import api.sistema.hidro.catalogo.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, Long> {
}
