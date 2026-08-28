package api.sistema.hidro.repository;

import api.sistema.hidro.entity.ConexaoTrechoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConexaoTrechoRepository extends JpaRepository<ConexaoTrechoEntity, Long> {

    List<ConexaoTrechoEntity> findByTrechoIdOrderByIdAsc(Long trechoId);

    void deleteByTrechoIdIn(List<Long> trechoIds);
}
