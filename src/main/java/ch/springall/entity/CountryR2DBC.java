package ch.springall.entity;

// Implémentation d'une entité Country avec R2DBC :
// R2DBC (Reactive Relational Database Connectivity) est une API réactive pour les bases de données relationnelles.
// Elle permet de gérer les opérations de base de données de manière non bloquante et asynchrone, ce qui est particulièrement utile pour les applications réactives.
// IL n'y a pas d'annotations spécifiques dans cette classe, mais en général,
// on utiliserait des annotations comme @Table pour définir la table de la base de données,
// L'annotation @Entity n'est pas utilisée avec R2DBC, mais on peut utiliser des annotations de Spring Data R2DBC comme @Id pour définir la clé primaire.

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "country")
public class CountryR2DBC {

    // avec R2DBC, on n'utilise pas @GeneratedValue, car la génération d'id est gérée différemment
    // ici on suppose que l'id est géré par la base de données elle-même (auto-increment, séquence, etc.)
    // la seule annotation nécessaire est @Id pour indiquer la clé primaire
    @Id
    @Column("country_id")
    private Long id;

    // Le reste des champs est similaire à une entité JPA classique : on utilise @Column pour définir les colonnes
    // et leurs contraintes : on peut utilise les annotations de validation spring comme @NotNull, @Size, etc.
    @Column(value = "country_name")
    @NotNull
    @Size(min = 2, max = 50)
    private String countryName;

    @Column(value = "country_continent")
    @NotNull
    private Continent continent;

    // Pour les constructeurs avec r2dbc, il est recommandé d'avoir un constructeur par défaut (sans arguments)
    // et un constructeur avec tous les arguments pour faciliter la création d'instances (même avec l'id)
    public CountryR2DBC() {
    }

    public CountryR2DBC(Long id, String countryName, Continent continent) {
        this.id = id;
        this.countryName = countryName;
        this.continent = continent;
    }

    // Getters et Setters : on peut les générer


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Continent getContinent() {
        return continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }
}
