package api.sistema.hidro.repository;

import api.sistema.hidro.model.EmpreendimentoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpreendimentoRepository extends JpaRepository<EmpreendimentoModel, Long> {
    List<EmpreendimentoModel> findByEmpresaIdAndAtivoTrue(Long empresaId);
}