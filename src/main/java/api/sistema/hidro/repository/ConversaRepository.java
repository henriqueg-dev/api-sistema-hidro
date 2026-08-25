package api.sistema.hidro.repository;

import api.sistema.hidro.entity.ConversaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversaRepository extends JpaRepository<ConversaEntity, Long> {

    List<ConversaEntity> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);
}
