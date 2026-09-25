package api.sistema.hidro.repository;

import api.sistema.hidro.entity.RecalqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecalqueRepository extends JpaRepository<RecalqueEntity, Long> {

    List<RecalqueEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);

    long countByEmpreendimentoAtivoTrue();
}
