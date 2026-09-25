package api.sistema.hidro.catalogo.entity;

import api.sistema.hidro.enums.PlanoAssinatura;
import api.sistema.hidro.enums.StatusAssinatura;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/** Uma por conta — o id é o próprio id da conta, não uma sequência à parte. */
@Entity
@Table(name = "tb_assinatura")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssinaturaEntity {

    @Id
    private Long contaId;

    /** Nulo até a primeira cobrança ser gerada — a conta ainda não escolheu um plano. */
    @Enumerated(EnumType.STRING)
    private PlanoAssinatura plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssinatura status;

    @Column(name = "expira_em")
    private LocalDateTime expiraEm;

    /** Id da cobrança PIX (Checkout Transparente) mais recente gerada na AbacatePay. */
    @Column(name = "abacatepay_cobranca_id")
    private String abacatePayCobrancaId;

    /** Última cobrança já confirmada pelo webhook — evita processar a mesma duas vezes. */
    @Column(name = "ultima_cobranca_confirmada_id")
    private String ultimaCobrancaConfirmadaId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

    /** ATIVA com expira_em vencido ou vazio conta como EXPIRADA, sem esperar o job diário. */
    public StatusAssinatura statusEfetivo() {
        boolean vencida = expiraEm == null || !expiraEm.isAfter(LocalDateTime.now());
        return status == StatusAssinatura.ATIVA && vencida ? StatusAssinatura.EXPIRADA : status;
    }
}
