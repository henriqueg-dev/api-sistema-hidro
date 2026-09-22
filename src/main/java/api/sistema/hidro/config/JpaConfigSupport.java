package api.sistema.hidro.config;

import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Fábrica de {@link EntityManagerFactoryBuilder} compartilhada entre catálogo e tenant — sem
 * isso, cada {@code LocalContainerEntityManagerFactoryBean} construído na mão perde a
 * conversão camelCase → snake_case que a autoconfiguração padrão do Spring Boot aplicaria de
 * graça (não podemos depender dela aqui: ver {@code SistemaHidroApplication}).
 */
@Component
public class JpaConfigSupport {

    private static final Map<String, Object> PROPRIEDADES_COMUNS = Map.of(
            "hibernate.physical_naming_strategy",
            "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

    /** Cada chamada devolve um builder novo — {@code .dataSource(...).build()} é de uso único. */
    public EntityManagerFactoryBuilder builder() {
        return new EntityManagerFactoryBuilder(
                new HibernateJpaVendorAdapter(), dataSource -> PROPRIEDADES_COMUNS, null);
    }
}
