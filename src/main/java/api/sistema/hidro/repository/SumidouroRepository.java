package api.sistema.hidro.repository;

import api.sistema.hidro.entity.SumidouroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SumidouroRepository extends JpaRepository<SumidouroEntity, Long> {

    List<SumidouroEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);

    long countByEmpreendimentoAtivoTrue();
}
