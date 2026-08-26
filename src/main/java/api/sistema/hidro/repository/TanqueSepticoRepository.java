package api.sistema.hidro.repository;

import api.sistema.hidro.entity.TanqueSepticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TanqueSepticoRepository extends JpaRepository<TanqueSepticoEntity, Long> {

    List<TanqueSepticoEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);
}
