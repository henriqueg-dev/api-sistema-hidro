package api.sistema.hidro.repository;

import api.sistema.hidro.entity.RamalPredialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RamalPredialRepository extends JpaRepository<RamalPredialEntity, Long> {

    List<RamalPredialEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);
}
