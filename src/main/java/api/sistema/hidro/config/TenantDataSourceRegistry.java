package api.sistema.hidro.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mapa {@code contaId -> DataSource} que {@link TenantRoutingDataSource} lê direto (put/get de
 * ConcurrentHashMap já garante visibilidade entre threads) — não passa pelo cache interno do
 * AbstractRoutingDataSource (resolvedDataSources), que não é seguro pra atualização em runtime.
 */
@Component
public class TenantDataSourceRegistry {

    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceRegistry.class);

    private final Map<Long, DataSource> registrados = new ConcurrentHashMap<>();

    public void registrar(Long contaId, DataSource dataSource) {
        registrados.put(contaId, dataSource);
        log.info("DataSource da conta {} registrado ({} conta(s) ativa(s))", contaId, registrados.size());
    }

    public boolean estaRegistrada(Long contaId) {
        return registrados.containsKey(contaId);
    }

    /** Usado só por {@link TenantJpaConfig} pra montar o TenantRoutingDataSource. */
    Map<Long, DataSource> mapa() {
        return registrados;
    }
}
