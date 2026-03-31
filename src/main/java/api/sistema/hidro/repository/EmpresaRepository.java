package api.sistema.hidro.repository;

import api.sistema.hidro.model.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpresaRepository extends JpaRepository<EmpresaModel, Long> {
    List<EmpresaModel> findByAtivoTrue();
}