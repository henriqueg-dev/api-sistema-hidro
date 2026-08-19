package api.sistema.hidro.repository;

import api.sistema.hidro.entity.CaixaGorduraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaixaGorduraRepository extends JpaRepository<CaixaGorduraEntity, Long> {

    List<CaixaGorduraEntity> findByEmpreendimentoIdOrderByCriadoEmAsc(Long empreendimentoId);

    long countByEmpreendimentoId(Long empreendimentoId);
}
