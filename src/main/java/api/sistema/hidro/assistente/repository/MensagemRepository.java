package api.sistema.hidro.assistente.repository;

import api.sistema.hidro.assistente.entity.MensagemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemEntity, Long> {

    List<MensagemEntity> findByConversaIdOrderByCriadoEmAsc(Long conversaId);

    void deleteByConversaId(Long conversaId);
}
