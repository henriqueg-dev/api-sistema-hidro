package api.sistema.hidro.catalogo.service;

import api.sistema.hidro.config.TenantDataSourceRegistry;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * Religa o {@link DataSource} de cada escritório no roteador a partir do banco físico já
 * existente (um por conta, nomeado hidro_tenant_&lt;id&gt;). Escritório novo é criado clonando um
 * banco de tenant existente como template — não tem provisionamento automático por aqui.
 */
@Service
public class TenantProvisionamentoService {

    private final TenantDataSourceRegistry registry;

    @Value("${spring.datasource.url}")
    private String urlCatalogo;

    @Value("${spring.datasource.username}")
    private String usuario;

    @Value("${spring.datasource.password}")
    private String senha;

    public TenantProvisionamentoService(TenantDataSourceRegistry registry) {
        this.registry = registry;
    }

    public static String nomeBanco(Long contaId) {
        return "hidro_tenant_" + contaId;
    }

    /** Usado no boot para religar o DataSource de uma conta já existente. */
    public void reconectar(Long contaId) {
        registry.registrar(contaId, criarDataSource(nomeBanco(contaId)));
    }

    private DataSource criarDataSource(String nomeBanco) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(substituirBanco(urlCatalogo, nomeBanco));
        dataSource.setUsername(usuario);
        dataSource.setPassword(senha);
        dataSource.setPoolName("hikari-tenant-" + nomeBanco);
        // Um tenant típico tem poucos usuários simultâneos — teto baixo evita esgotar as
        // conexões do Postgres quando há muitos escritórios ativos ao mesmo tempo.
        dataSource.setMaximumPoolSize(5);
        return dataSource;
    }

    /** Troca o nome do banco na URL JDBC, preservando host, porta e parâmetros de query. */
    private String substituirBanco(String url, String novoBanco) {
        int interrogacao = url.indexOf('?');
        String semQuery = interrogacao >= 0 ? url.substring(0, interrogacao) : url;
        String query = interrogacao >= 0 ? url.substring(interrogacao) : "";
        int ultimaBarra = semQuery.lastIndexOf('/');
        return semQuery.substring(0, ultimaBarra + 1) + novoBanco + query;
    }
}
