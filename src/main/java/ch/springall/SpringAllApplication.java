package ch.springall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

// Classe principale de l'application Spring Boot
// L'annotation @SpringBootApplication indique qu'il s'agit d'une application Spring Boot
// et active la configuration automatique, le balayage des composants et la configuration des propriétés.
@SpringBootApplication
@Import(DataSourceAutoConfiguration.class)
@EnableJpaRepositories(basePackages = "ch.springall.repository.jpa")
//@EnableR2dbcRepositories(basePackages = "ch.springall.repository.r2dbc")
public class SpringAllApplication {
    public static void main(String[] args) {
        // Démarrage de l'application Spring Boot
        SpringApplication.run(SpringAllApplication.class, args);
    }

}
