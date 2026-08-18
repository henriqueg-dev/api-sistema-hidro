package api.sistema.hidro.repository;

import api.sistema.hidro.entity.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaEntity, Long> {
    List<EmpresaEntity> findByAtivoTrue();
}