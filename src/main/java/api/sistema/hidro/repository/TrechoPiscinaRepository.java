package api.sistema.hidro.repository;

import api.sistema.hidro.entity.TrechoPiscinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrechoPiscinaRepository extends JpaRepository<TrechoPiscinaEntity, Long> {

    List<TrechoPiscinaEntity> findByPiscinaIdOrderByOrdemAsc(Long piscinaId);

    void deleteByPiscinaId(Long piscinaId);
}
