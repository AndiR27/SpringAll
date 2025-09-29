package ch.springall.entity;

import jakarta.persistence.*;
import org.springframework.boot.jackson.JsonMixin;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "director")
public class Director extends Person{

    @Column(name = "oscar_count", nullable = true)
    private int oscarCount;

    //relation avec Movie : un directeur peut gérer plusieurs films @OneToMany
    //mappedBy = "director" fait référence à l'attribut "director" dans la classe Movie
    //le mapping permet de définir qui est le propriétaire de la relation (ici c'est Director)
    // on peut ajouter cascade pour propager les opérations (persist, remove, etc.) aux entités associées
    // et fetch pour définir la stratégie de chargement (EAGER ou LAZY)
    @OneToMany(mappedBy = "director", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Movie> moviesDirected; // La liste est initialisée par défaut à null, mais JPA la gère automatiquement

    public Director() {
        super();
    }

    public Director(String firstName, String lastName, LocalDate birthDate, int oscarCount) {
        super(firstName, lastName, birthDate);
        this.oscarCount = oscarCount;
    }

    public int getOscarCount() {
        return oscarCount;
    }

    public void setOscarCount(int oscarCount) {
        this.oscarCount = oscarCount;
    }

    public List<Movie> getMoviesDirected() {
        return moviesDirected;
    }

    public void setMoviesDirected(List<Movie> moviesDirected) {
        this.moviesDirected = moviesDirected;
    }
}
