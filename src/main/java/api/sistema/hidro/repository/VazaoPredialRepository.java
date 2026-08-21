package api.sistema.hidro.repository;

import api.sistema.hidro.entity.VazaoPredialEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VazaoPredialRepository extends JpaRepository<VazaoPredialEntity, Long> {

    List<VazaoPredialEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);
}
