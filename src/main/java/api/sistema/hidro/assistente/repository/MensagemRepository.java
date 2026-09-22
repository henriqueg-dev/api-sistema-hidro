package api.sistema.hidro.assistente.repository;

import api.sistema.hidro.assistente.entity.MensagemEntity;
import api.sistema.hidro.assistente.enums.PapelMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemEntity, Long> {

    List<MensagemEntity> findByConversaIdOrderByCriadoEmAsc(Long conversaId);

    void deleteByConversaId(Long conversaId);

    /** Usado pra checar o limite mensal do plano (ver PlanoAssinatura.limiteMensagensAssistenteMes). */
    long countByPapelAndCriadoEmGreaterThanEqual(PapelMensagem papel, LocalDateTime desde);
}
