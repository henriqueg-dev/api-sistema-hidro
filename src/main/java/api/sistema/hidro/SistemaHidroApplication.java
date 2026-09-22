package api.sistema.hidro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * DataSource, JPA e repositórios são configurados manualmente (ver {@code config/}) porque a
 * aplicação fala com dois bancos de naturezas diferentes: o catálogo (fixo) e o de cada tenant
 * (roteado em runtime). Deixar a autoconfiguração padrão cuidar de um dos dois não funciona bem
 * aqui: ela usa {@code @ConditionalOnMissingBean} por TIPO (não por nome) para
 * {@code LocalContainerEntityManagerFactoryBean}/{@code DataSource} — a mera presença do bean
 * manual do tenant faz a condição falhar e cancela a criação do bean autoconfigurado do
 * catálogo. Por isso os dois são construídos da mesma forma, manualmente.
 *
 * <p>{@code @EnableTransactionManagement} precisa ser explícito pelo mesmo motivo: a versão
 * autoconfigurada só se ativa condicionada a um DataSource/EntityManagerFactory autoconfigurado.
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataJpaRepositoriesAutoConfiguration.class
})
@EnableTransactionManagement
@EnableScheduling
public class SistemaHidroApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaHidroApplication.class, args);
    }

}
