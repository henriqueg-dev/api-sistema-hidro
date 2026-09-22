package api.sistema.hidro.catalogo.repository;

import api.sistema.hidro.catalogo.entity.AssinaturaEntity;
import api.sistema.hidro.enums.StatusAssinatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AssinaturaRepository extends JpaRepository<AssinaturaEntity, Long> {

    List<AssinaturaEntity> findByStatusAndExpiraEmBefore(StatusAssinatura status, LocalDateTime instante);
}
