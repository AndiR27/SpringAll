package ch.springall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe principale de l'application Spring Boot
// L'annotation @SpringBootApplication indique qu'il s'agit d'une application Spring Boot
// et active la configuration automatique, le balayage des composants et la configuration des propriétés.
@SpringBootApplication
public class SpringAllApplication {
    public static void main(String[] args) {
        // Démarrage de l'application Spring Boot
        SpringApplication.run(SpringAllApplication.class, args);
    }

}
