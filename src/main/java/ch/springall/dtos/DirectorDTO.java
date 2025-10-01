package ch.springall.dtos;

// Utilisation d'un DTO classique pour l'entité Director (Director)
// Le DTO est une classe simple qui ne contient que des attributs et des accesseurs (getters et setters).
// Il n'y a pas de logique métier dans un DTO. Il sert uniquement à transférer des données entre les couches de l'application.

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DirectorDTO {
    // les attributs doivent correspondre aux attributs de l'entité Director
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private int oscarCount;
    private List<MovieDTO> moviesDirected; // Liste des films réalisés par le directeur

    // Constructeurs : un constructeur par défaut (sans arguments) et un constructeur avec tous les arguments
    public DirectorDTO() {
    }

    public DirectorDTO(Long id, String firstName, String lastName, LocalDate birthDate, int oscarCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.oscarCount = oscarCount;
        this.moviesDirected = new ArrayList<>();
    }

    // Getters et Setters pour tous les attributs
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getOscarCount() {
        return oscarCount;
    }

    public void setOscarCount(int oscarCount) {
        this.oscarCount = oscarCount;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<MovieDTO> getMoviesDirected() {
        return moviesDirected;
    }

    public void setMoviesDirected(List<MovieDTO> moviesDirected) {
        this.moviesDirected = moviesDirected;
    }
}
