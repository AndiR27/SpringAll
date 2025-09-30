package ch.springall.repository;

import ch.springall.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Interface Repository pour l'entité Movie avec l'annotation @Repository
// et l'extension de JpaRepository pour fournir des opérations CRUD
// sur les objets Movie avec des identifiants de type Long
// -> Spring Data JPA génère automatiquement l'implémentation de cette interface
// et l'enregistre comme un bean Spring grâce à l'annotation @Repository
@Repository
public interface RepositoryMovie extends JpaRepository<Movie, Long> {

    //v1 : Méthode dérivée par nom
    // Méthode personnalisée pour trouver un film par son titre : Movie findByTitle(String title);
    // la nomenclature "findBy" est reconnue par Spring Data JPA pour générer la requête correspondante
    // -> Cela permet de rechercher un film dans la base de données en fonction de son titre
    //Fonctionnement : Spring parse le nom, identifie les champs et construit la requête JPQL.
    Movie findByTitle(String title);

    //v2 : Méthode avec @Query avec JPQL
    // Méthode personnalisée pour trouver un film par son titre en utilisant une requête JPQL
    // on utilise l'annotation @Query pour définir la requête, Spring utilise l'EntityManager pour l'exécuter
    // Syntaxe plus lourde, mais contrôle total indépendant de la base SQL
    @Query("Select m from Movie m where m.title = :title")
    Movie findByMovieQuery(@Param("title") String title);

    //v3 : Méthode avec @Query avec SQL natif
    // Méthode personnalisée pour trouver un film par son titre en utilisant une requête SQL native
    // Spring transmet la requete telle quelle au driver jdbc, sans conversion JPQL
    @Query(value = "Select * from movie where title = :title", nativeQuery = true)
    Movie findByMovieNativeQuery(@Param("title") String title);

    //v4 Methode custom avec JPQL pour chercher un film par son titre
    // On peut aussi coder l'implémentation nous-même dans une classe séparée
    // On crée une interface custom avec la méthode, puis une classe qui implémente cette interface
    // et on utilise @Autowired pour injecter le RepositoryMovie dans cette classe
    // On va plutôt utiliser cette méthode pour des requêtes complexes et jamais implémenter un JPARepository en entier
    // car on perd tous les avantages de Spring Data JPA et il y a trop de méthodes à implémenter
    Movie findByTitleCustom(String title);
}
