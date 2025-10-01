package ch.springall.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

// Classe représentant une entité Movie dans la base de données
// L'annotation @Entity indique que cette classe est une entité JPA
@Entity
@Table(name = "movie") // Spécifie le nom de la table dans la base de données
public class Movie {

    //Une entity doit forcement avoir un id
    //on laisse JPA gérer la génération de l'id automatiquement
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // L'annotation @Column spécifie les détails de la colonne dans la table
    // on peut mettre des contraintes comme nullable = false, unique = true, etc.
    @Column(name = "title", nullable = false)
    private String title;

    // Des champs spécifiques comme une date peuvent nécessiter un formatage particulier
    //ou des annotations supplémentaires tel que @Temporal pour les dates
    // ici on utilise LocalDateTime pour stocker la date et l'heure de sortie du film
    @Column(name = "release_date", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy:HH:mm")
    private LocalDateTime releaseDate;

    // Pour des enums : on peut utiliser @Enumerated pour spécifier comment l'enum doit être stocké
    // ici on choisit de stocker l'enum sous forme de String (ou de Ordinal : 0, 1, 2, ...)
    // on crée une enum Genre dans un fichier séparé
    // important : il faut que l'enum soit dans le même package ou dans un package importé
    // et qu'il soit public
    @Enumerated(EnumType.STRING)
    @Column(name = "genre", nullable = false)
    private Genre genre;

    // On peut aussi avoir des champs avec des types plus basiques
    // comme des int, double, boolean, etc.
    // -> par défaut, JPA mappe les types Java aux types SQL correspondants
    // ici on utilise double pour stocker la note du film, par exemple entre 0.0 et 10.0
    // donc on peut mettre des contraintes comme min = 0.0, max = 10.0
    // avec des annotations de validation spring comme @Min, @Max
    //
    @Column(name = "rating")
    @Min(0)
    @Max(10)
    private double rating;

    // Pour une relation avec une autre entité, on utiliserait des annotations comme @ManyToOne, @OneToMany, etc.
    // on déclare le type de relation, puis on ajoute une référence à l'autre entité
    // l'annotation de relation peut être complétée par des paramètres comme cascade, fetch, etc.
    // on définit aussi le mapping pour la gestion de la clé étrangère
    //Dans cet exemple, un film a un réalisateur (ManyToOne), et un réalisateur peut avoir plusieurs films (OneToMany)
    // donc la foreign key sera dans la table movie
    @ManyToOne
    @JoinColumn(name = "director_id") // Spécifie la colonne de clé étrangère dans la table movie
    private Director director;

    // Constructeur par défaut requis par JPA : public et sans arguments
    // -> Pourquoi : JPA utilise la réflexion pour instancier les entités
    // et a besoin d'un constructeur sans arguments
    public Movie() {
    }

    // Constructeur avec tous les champs sauf l'id (généré automatiquement)
    // -> Utile pour créer des instances de Movie facilement dans le code
    public Movie(String title, LocalDateTime releaseDate, Genre genre, double rating, Director director) {
        this.title = title;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.rating = rating;
        this.director = director;
    }


    // Getters et Setters pour tous les champs
    // -> Nécessaires pour que JPA puisse accéder aux champs privés


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }


}
