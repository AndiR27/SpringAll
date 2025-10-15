package ch.springall.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Cette classe configure la sécurité de l'application Spring Boot
// L'annotation @Configuration indique qu'il s'agit d'une classe de configuration Spring
// L'annotation @EnableWebSecurity active la sécurité web dans l'application
// On peut également utiliser @EnableMethodSecurity pour activer la sécurité au niveau des méthodes (annotations @PreAuthorize, @PostAuthorize, etc.)
// On va définir un bean SecurityFilterChain pour configurer les règles de sécurité HTTP et l'authentification
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    // Implémentation du filtre de sécurité avec SecurityFilterChain
    //Le bean permet à Spring de gérer l'instance de SecurityFilterChain et
    // de l'injecter dans les composants qui en ont besoin (comme les contrôleurs ou les services)

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        //Dans cette methode : on configure les règles de sécurité HTTP
        //Par défaut, Spring Security protège toutes les routes, mais on peut personnaliser cela
        //On va par exemple autoriser l'accès à certaines routes publiques (ex: /login, /register)
        http.authorizeHttpRequests(auth ->
                // Permet d'accéder à la page d'accueil sans être authentifié
                        auth.requestMatchers("/home").permitAll().
                // On demande pour toutes les requêtes d'être authentifiées
                        anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults());// Page de login personnalisée
        return http.build();
    }

    //

}
