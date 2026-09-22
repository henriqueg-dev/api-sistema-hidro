package api.sistema.hidro.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * O banco de metadado do sistema: contas, assinaturas, índice de login. Único, fixo — sem
 * roteamento. Construído manualmente pela mesma razão descrita em
 * {@link api.sistema.hidro.SistemaHidroApplication}.
 */
@Configuration
@EnableJpaRepositories(
        basePackages = "api.sistema.hidro.catalogo.repository",
        entityManagerFactoryRef = "catalogoEntityManagerFactory",
        transactionManagerRef = "catalogoTransactionManager")
public class CatalogoJpaConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Primary
    @Bean
    public DataSource catalogoDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public Object catalogoFlywayMigration(DataSource catalogoDataSource) {
        Flyway.configure()
                .dataSource(catalogoDataSource)
                .locations("classpath:db/catalogo")
                .load()
                .migrate();
        return new Object();
    }

    @Primary
    @Bean
    @DependsOn("catalogoFlywayMigration")
    public LocalContainerEntityManagerFactoryBean catalogoEntityManagerFactory(
            JpaConfigSupport jpaConfigSupport, DataSource catalogoDataSource) {
        Map<String, Object> propriedades = new HashMap<>();
        // O Flyway assume o schema; o Hibernate só confirma que bate com as entidades.
        propriedades.put("hibernate.hbm2ddl.auto", "validate");

        return jpaConfigSupport.builder()
                .dataSource(catalogoDataSource)
                .packages("api.sistema.hidro.catalogo.entity")
                .persistenceUnit("catalogo")
                .properties(propriedades)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager catalogoTransactionManager(
            @Qualifier("catalogoEntityManagerFactory") EntityManagerFactory catalogoEntityManagerFactory) {
        return new JpaTransactionManager(catalogoEntityManagerFactory);
    }
}
