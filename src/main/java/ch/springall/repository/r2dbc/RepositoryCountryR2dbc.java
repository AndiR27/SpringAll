package ch.springall.repository.r2dbc;

import ch.springall.entity.Continent;
import ch.springall.entity.CountryR2DBC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// Interface pour le repository des pays utilisant R2DBC (Reactive Relational Database Connectivity)
// Equivalent à JPA, mais on va étendre ReactiveCrudRepository
public interface RepositoryCountryR2dbc extends ReactiveCrudRepository<CountryR2DBC, Long> {
    // ReactiveCrudRepository fournit des méthodes CRUD réactives pour l'entité CountryR2dbc

    // Méthode permettant de récupérer un pays par son nom de manière réactive
    // Le préfixe "findBy" est reconnu par Spring Data pour générer la requête correspondante
    // La grosse différence est que la méthode retourne un Mono<CountryR2DBC> au lieu de CountryR2DBC
    Mono<CountryR2DBC> findByCountryName(String countryName);

    // On peut ajouter d'autres variantes de méthodes personnalisées si nécessaire avec @Query
    // par exemple pour des requêtes plus complexes : le principe est le même que pour JPA
    // et on retourne toujours des Mono ou Flux pour les résultats
    @Query("SELECT * FROM country WHERE country_name = :countryName")
    Mono<CountryR2DBC> findByCountryNameQuery(@Param("countryName") String countryName);

    // Lorsqu'on retourne plusieurs résultats, on utilise Flux<CountryR2DBC> au lieu de List<CountryR2DBC>
    // un Flux représente une séquence asynchrone de 0 à N éléments
    // ici on cherche tous les pays d'un continent donné
    Flux<CountryR2DBC> findAllByContinent(Continent continent);
}
