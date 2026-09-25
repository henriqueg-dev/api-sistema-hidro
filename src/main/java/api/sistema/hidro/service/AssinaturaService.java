package api.sistema.hidro.service;

import api.sistema.hidro.catalogo.entity.AssinaturaEntity;
import api.sistema.hidro.catalogo.repository.AssinaturaRepository;
import api.sistema.hidro.dto.AssinaturaResponseDTO;
import api.sistema.hidro.dto.CobrancaPixResponseDTO;
import api.sistema.hidro.enums.PlanoAssinatura;
import api.sistema.hidro.enums.StatusAssinatura;
import api.sistema.hidro.exception.RecursoNaoEncontradoException;
import api.sistema.hidro.security.TenantContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(value = "catalogoTransactionManager", readOnly = true)
public class AssinaturaService {

    private static final Logger log = LoggerFactory.getLogger(AssinaturaService.class);

    private final AssinaturaRepository assinaturaRepository;
    private final AbacatePayService abacatePayService;

    public AssinaturaResponseDTO statusAtual() {
        AssinaturaEntity assinatura = buscarAtual();
        return new AssinaturaResponseDTO(assinatura.getPlano(), assinatura.statusEfetivo(), assinatura.getExpiraEm());
    }

    @Transactional("catalogoTransactionManager")
    public CobrancaPixResponseDTO gerarCobranca(PlanoAssinatura plano) {
        AssinaturaEntity assinatura = buscarAtual();

        AbacatePayService.CobrancaPix cobranca = abacatePayService.criarCobranca(assinatura.getContaId(), plano);

        // O plano só muda no pagamento: gravá-lo aqui liberaria os limites dele sem pagar.
        assinatura.setAbacatePayCobrancaId(cobranca.id());
        assinaturaRepository.save(assinatura);

        log.info("Cobrança gerada: conta {} plano {} cobrança {}", assinatura.getContaId(), plano, cobranca.id());
        return new CobrancaPixResponseDTO(cobranca.brCode(), cobranca.brCodeBase64());
    }

    /**
     * Chamado pelo webhook depois que o HMAC já foi validado — quem autentica o pagamento é a
     * assinatura HMAC do corpo inteiro, não bater com um id de cobrança guardado localmente (por
     * isso não compara com abacatePayCobrancaId: gerar uma 2ª cobrança sem pagar a 1ª não pode
     * fazer um pagamento real da 1ª ser perdido). Idempotente via ultimaCobrancaConfirmadaId:
     * reentrega do mesmo evento não estende a assinatura duas vezes.
     */
    @Transactional("catalogoTransactionManager")
    public void confirmarPagamento(Long contaId, String abacatePayCobrancaId, PlanoAssinatura planoPago) {
        AssinaturaEntity assinatura = assinaturaRepository.findById(contaId).orElse(null);
        if (assinatura == null) {
            log.warn("Webhook de pagamento para conta desconhecida: {}", contaId);
            return;
        }
        if (Objects.equals(assinatura.getUltimaCobrancaConfirmadaId(), abacatePayCobrancaId)) {
            log.info("Webhook duplicado ignorado: conta {} cobrança {} já confirmada", contaId, abacatePayCobrancaId);
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime baseRenovacao = assinatura.getExpiraEm() != null && assinatura.getExpiraEm().isAfter(agora)
                ? assinatura.getExpiraEm()
                : agora;
        StatusAssinatura statusAnterior = assinatura.getStatus();

        // Cobrança antiga, sem plano no metadata: mantém o plano atual.
        if (planoPago != null) {
            assinatura.setPlano(planoPago);
        }
        assinatura.setStatus(StatusAssinatura.ATIVA);
        assinatura.setExpiraEm(baseRenovacao.plusMonths(1));
        assinatura.setUltimaCobrancaConfirmadaId(abacatePayCobrancaId);
        assinaturaRepository.save(assinatura);

        log.info("Pagamento confirmado: conta {} cobrança {} plano {} — status {} -> ATIVA, expira em {}",
                contaId, abacatePayCobrancaId, assinatura.getPlano(), statusAnterior, assinatura.getExpiraEm());
    }

    /** Roda uma vez por dia; contas vencidas perdem acesso até gerar e pagar nova cobrança. */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional("catalogoTransactionManager")
    public void expirarVencidas() {
        List<AssinaturaEntity> vencidas =
                assinaturaRepository.findByStatusAndExpiraEmBefore(StatusAssinatura.ATIVA, LocalDateTime.now());

        vencidas.forEach(assinatura -> assinatura.setStatus(StatusAssinatura.EXPIRADA));
        assinaturaRepository.saveAll(vencidas);

        if (!vencidas.isEmpty()) {
            log.info("Assinaturas expiradas nesta rodada: {}",
                    vencidas.stream().map(AssinaturaEntity::getContaId).toList());
        }
    }

    /** Usado por outros services do tenant pra checar limites do plano (ver PlanoAssinatura). */
    public PlanoAssinatura planoAtual() {
        return buscarAtual().getPlano();
    }

    private AssinaturaEntity buscarAtual() {
        return assinaturaRepository.findById(TenantContext.atual())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Assinatura não encontrada"));
    }
}
