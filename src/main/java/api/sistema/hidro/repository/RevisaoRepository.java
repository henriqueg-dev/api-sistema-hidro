package api.sistema.hidro.repository;

import api.sistema.hidro.entity.RevisaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevisaoRepository extends JpaRepository<RevisaoEntity, Integer> {

    List<RevisaoEntity> findTop200ByOrderByIdDesc();

    List<RevisaoEntity> findAllByOrderByIdDesc();
}
