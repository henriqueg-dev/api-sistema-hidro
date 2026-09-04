package api.sistema.hidro.repository;

import api.sistema.hidro.entity.OrcamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrcamentoRepository extends JpaRepository<OrcamentoEntity, Long> {

    List<OrcamentoEntity> findAllByOrderByCriadoEmDesc();
}
