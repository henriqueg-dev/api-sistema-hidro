package api.sistema.hidro.config;

import api.sistema.hidro.security.TenantContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Cada escritório tem seu próprio banco Postgres. Toda query do pacote de negócio passa por
 * aqui, que resolve a conexão certa a partir de {@link TenantContext}.
 *
 * <p>Não usa o cache interno (resolvedDataSources) do AbstractRoutingDataSource — não é volatile,
 * então uma thread pode não enxergar uma conta registrada por outra. Lê direto do
 * ConcurrentHashMap do {@link TenantDataSourceRegistry}, que já dá a visibilidade certa.
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private final Map<Long, DataSource> registrados;
    private final DataSource bootstrap;

    public TenantRoutingDataSource(Map<Long, DataSource> registrados, DataSource bootstrap) {
        this.registrados = registrados;
        this.bootstrap = bootstrap;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.atualOuNulo();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Long contaId = (Long) determineCurrentLookupKey();
        // Nenhum tenant está registrado no instante em que o Hibernate inspeciona o banco no
        // boot (isso só acontece depois do contexto Spring terminar de subir) — usa o bootstrap
        // pra descobrir o dialect. Nunca serve dado de negócio: toda query real roda com
        // TenantContext já setado.
        if (contaId == null) {
            return bootstrap;
        }
        DataSource dataSource = registrados.get(contaId);
        if (dataSource == null) {
            throw new IllegalStateException("Nenhum DataSource registrado para a conta " + contaId);
        }
        return dataSource;
    }

    @Override
    public void afterPropertiesSet() {
        // Sem cache interno pra resolver (ver determineTargetDataSource) — nada a fazer aqui;
        // sem isso o InitializingBean da superclasse falha no boot exigindo targetDataSources.
    }
}
