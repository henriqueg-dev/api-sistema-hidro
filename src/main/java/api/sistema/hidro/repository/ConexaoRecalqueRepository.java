package api.sistema.hidro.repository;

import api.sistema.hidro.entity.ConexaoRecalqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConexaoRecalqueRepository extends JpaRepository<ConexaoRecalqueEntity, Long> {

    List<ConexaoRecalqueEntity> findByRecalqueIdOrderByIdAsc(Long recalqueId);

    void deleteByRecalqueId(Long recalqueId);
}
