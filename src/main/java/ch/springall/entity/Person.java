package ch.springall.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

// Annotation indiquant que cette classe est une superclasse mappée
// Les entités qui héritent de cette classe hériteront de ses mappings
// et de ses propriétés
@MappedSuperclass
@Table(name = "person")
public abstract class Person {

    // Clé primaire de l'entité : pour une superclasse, on peut laisser
    // la génération de l'id aux sous-classes via GenerationType.IDENTITY
    // ce qui permet à chaque sous-classe d'avoir sa propre stratégie de génération
    // sans avoir à redéfinir l'id dans chaque sous-classe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;

    public Person() {
    }

    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
