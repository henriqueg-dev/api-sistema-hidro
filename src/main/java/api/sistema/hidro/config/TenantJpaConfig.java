package api.sistema.hidro.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Onde vive Cliente, Empreendimento, cálculos, auditoria e o assistente — tudo o que pertence a
 * um escritório específico. Roteado por {@link TenantRoutingDataSource} a partir do contaId da
 * requisição; cada banco de tenant é migrado por Flyway no provisionamento, não pelo Hibernate.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = {"api.sistema.hidro.repository", "api.sistema.hidro.assistente.repository"},
        entityManagerFactoryRef = "tenantEntityManagerFactory",
        transactionManagerRef = "tenantTransactionManager")
public class TenantJpaConfig {

    @Value("${spring.datasource.url}")
    private String urlCatalogo;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public TenantRoutingDataSource tenantRoutingDataSource(TenantDataSourceRegistry registry) {
        return new TenantRoutingDataSource(registry.mapa(), bootstrapDataSource());
    }

    private HikariDataSource bootstrapDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(urlCatalogo);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setPoolName("hikari-tenant-bootstrap");
        dataSource.setMaximumPoolSize(2);
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(
            JpaConfigSupport jpaConfigSupport, TenantRoutingDataSource tenantRoutingDataSource) {
        Map<String, Object> propriedades = new HashMap<>();
        // O schema de cada tenant vem das migrations Flyway aplicadas no provisionamento, não
        // do Hibernate — bancos diferentes por baixo do mesmo roteador tornam "validate" inútil.
        propriedades.put("hibernate.hbm2ddl.auto", "none");
        propriedades.put("org.hibernate.envers.audit_table_suffix", "_aud");
        propriedades.put("org.hibernate.envers.store_data_at_delete", "true");
        propriedades.put("org.hibernate.envers.global_with_modified_flag", "true");
        propriedades.put("org.hibernate.envers.track_entities_changed_in_revision", "true");

        return jpaConfigSupport.builder()
                .dataSource(tenantRoutingDataSource)
                .packages("api.sistema.hidro.entity", "api.sistema.hidro.assistente.entity")
                .persistenceUnit("tenant")
                .properties(propriedades)
                .build();
    }

    @Bean
    public PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantEntityManagerFactory") EntityManagerFactory tenantEntityManagerFactory) {
        return new JpaTransactionManager(tenantEntityManagerFactory);
    }
}
