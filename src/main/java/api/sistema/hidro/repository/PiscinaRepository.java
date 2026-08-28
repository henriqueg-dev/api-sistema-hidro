package api.sistema.hidro.repository;

import api.sistema.hidro.entity.PiscinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PiscinaRepository extends JpaRepository<PiscinaEntity, Long> {

    List<PiscinaEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);
}
