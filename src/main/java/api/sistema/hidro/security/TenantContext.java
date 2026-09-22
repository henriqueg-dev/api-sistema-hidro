package api.sistema.hidro.security;

/**
 * Qual banco de tenant a thread da requisição atual deve usar. Setado pelo {@code JwtFiltro} (a
 * partir do claim {@code contaId} do JWT) ou pelo próprio fluxo de login/registro antes de tocar
 * qualquer repositório do pacote de negócio — {@link TenantRoutingDataSource} lê daqui.
 */
public final class TenantContext {

    private static final ThreadLocal<Long> ATUAL = new ThreadLocal<>();

    private TenantContext() {
    }

    public static void definir(Long contaId) {
        ATUAL.set(contaId);
    }

    /** Usa quando o tenant é obrigatório no ponto de chamada — lança se ninguém setou. */
    public static Long atual() {
        Long contaId = ATUAL.get();
        if (contaId == null) {
            throw new IllegalStateException("Nenhuma conta definida no contexto atual");
        }
        return contaId;
    }

    /** Usado só pelo roteador de DataSource, que trata ausência como erro de configuração. */
    public static Long atualOuNulo() {
        return ATUAL.get();
    }

    /** Chamado ao fim de cada requisição — evita vazar o tenant entre requisições no mesmo thread do Tomcat. */
    public static void limpar() {
        ATUAL.remove();
    }
}
