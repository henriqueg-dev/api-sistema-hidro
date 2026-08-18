package api.sistema.hidro.repository;

import api.sistema.hidro.entity.EmpreendimentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpreendimentoRepository extends JpaRepository<EmpreendimentoEntity, Long> {
    List<EmpreendimentoEntity> findByEmpresaIdAndAtivoTrue(Long empresaId);
}