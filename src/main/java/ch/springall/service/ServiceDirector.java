package ch.springall.service;

import ch.springall.dtos.DirectorDTO;
import ch.springall.dtos.DirectorRecord;
import ch.springall.dtos.MovieRecord;
import ch.springall.entity.Director;
import ch.springall.entity.Movie;
import ch.springall.mapper.MapperDirector;
import ch.springall.mapper.MapperMovie;
import ch.springall.repository.jpa.RepositoryDirector;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Classe de service pour la gestion des directeurs : On ajoute ici les méthodes métiers (CRUD et autres)
// L'annotation @Service indique que cette classe est un service Spring et sera gérée par le conteneur Spring
@Service
public class ServiceDirector {

    // Pour accéder aux données, on injecte le repository correspondant (RepositoryDirector)
    // Il existe plusieurs façons de faire l'injection de dépendances en Spring :
    // - Injection par constructeur (recommandée) : on crée un constructeur avec le repository en paramètre
    // - Injection par champ : on utilise l'annotation @Autowired sur le champ du repository
    // - Injection par setter : on crée une méthode setter avec l'annotation @Autowired
    // - Injection par interface (moins courante) : on implémente une interface avec une méthode setter
    // On préfère l'injection par constructeur : c'est plus sûr, plus facile à tester et permet de définir des dépendances immuables
    // L'annotation @Autowired pose des problèmes de cyclic dependency et n'est pas recommandée pour les constructeurs

    // On définit ainsi le repository comme une dépendance finale (final)
    // et on l'initialise dans le constructeur
    private final RepositoryDirector repositoryDirector;

    //Mappers (si on utilise des DTOs)
    private final MapperDirector mapperDirector;

    //Autres services/mappers si besoin (pour gérer les relations par exemple)
    private final ServiceMovie serviceMovie;
    private final MapperMovie mapperMovie;

    public ServiceDirector(@Qualifier("jpaDirector") RepositoryDirector repositoryDirector, MapperDirector mapperDirector, ServiceMovie serviceMovie, MapperMovie mapperMovie) {
        this.repositoryDirector = repositoryDirector;
        this.mapperDirector = mapperDirector;
        this.serviceMovie = serviceMovie;
        this.mapperMovie = mapperMovie;
    }

    //Logging : dans Spring Boot, on utilise généralement SLF4J avec Logback (inclus par défaut)
    // On peut utiliser l'annotation @Slf4j de Lombok pour générer automatiquement un logger
    // ou créer un logger manuellement avec LoggerFactory
    private static final Logger logger = LoggerFactory.getLogger(ServiceDirector.class);


    // Méthodes pour ajouter et récupérer un/des directeurs

    //V1 : Utilisation classique de save() et findById() de JpaRepository
    // on retourne le résultat directement de la méthode du repository
    public Director addDirector(Director director) {
        return repositoryDirector.save(director);
    }

    public Director findDirectorById(Long id){
        // La méthode findById() retourne un Optional<Director> pour gérer le cas où l'id n'existe pas
        // ici on utilise get() pour récupérer le Director, mais cela peut lancer une exception si l'id n'existe pas
        return repositoryDirector.findById(id).get();
    }
    // Il s'agit ici d'une méthode dérivée par nom (findByFirstNameAndLastName) que l'on a définie dans RepositoryDirector
    // Spring Data JPA génère automatiquement l'implémentation de cette méthode et retourne le résultat
    public Director findByNames(String firstName, String lastName){
        return repositoryDirector.findByFirstNameAndLastName(firstName, lastName);
    }

    //Lorsque l'on ajoute ou modifie des données dans une méthode de service, on peut utiliser @Transactional
    // pour gérer les transactions automatiquement et éviter les problèmes de cohérence des données :
    // @Transactional : démarre une transaction au début de la méthode et la commit à la fin si tout s'est bien passé
    // De plus, si une exception est levée, la transaction est rollbackée automatiquement


    //V2 : utilisation des DTOs : on n'expose pas l'entité à la couche supérieure (REST), car
    // cela peut poser des problèmes de sécurité et de maintenance -> on crée une classe DirectorDTO
    // et on mappe les données entre l'entité et le DTO dans le service
    @Transactional
    public DirectorDTO addDirectorDTO(DirectorDTO directorDTO){
        // On mappe le DTO vers l'entité
        Director d = mapperDirector.fromDtoToEntity(directorDTO);

        // On sauvegarde l'entité
        Director savedDirector = repositoryDirector.save(d);

        // On mappe l'entité sauvegardée vers le DTO et on le retourne
        return mapperDirector.toDto(savedDirector);
    }

    //V3 : utilisation des Records (Java 16+) : même principe que les DTOs, mais avec une syntaxe plus concise
    // On crée une classe DirectorRecord et on mappe les données entre l'entité et le Record dans le service
    // Les Records sont immuables et plus légers que les DTOs classiques
    @Transactional
    public DirectorRecord addDirectorRecord(DirectorRecord directorRecord){
        // On mappe le Record vers l'entité
        Director d = mapperDirector.fromRecordToEntity(directorRecord);

        // On sauvegarde l'entité
        Director savedDirector = repositoryDirector.save(d);
        //log.info("director saved with id : {}", savedDirector.getId());
        // On mappe l'entité sauvegardée vers le Record et on le retourne
        return mapperDirector.toRecord(savedDirector);
    }

    //V4 : utilisation de Optional pour gérer le cas où l'id n'existe pas
    // Optional est une classe conteneur qui peut contenir une valeur ou être vide et qui existe depuis Java 8
    // Cela permet d'éviter les NullPointerException et de forcer le développeur à gérer le cas où la valeur est absente
    // avec des Exceptions/Try-Catch ou des méthodes comme orElse, orElseThrow, ifPresent, etc.
    // L'Optional est logique dans une recherche, car il peut échouer !!
    public Optional<DirectorRecord> findDirectorByIdOptional(Long id){
        // On utilise map() pour transformer l'Optional<Director> en Optional<DirectorRecord>
        Director d = repositoryDirector.findById(id).orElse(null);
        // On utilise ofNullable() pour gérer le cas où d est null
        return Optional.ofNullable(mapperDirector.toRecord(d));
    }

    //Autres méthodes (update, delete, findAll, etc.) à ajouter selon les besoins
    //update : pour une mise à jour, il suffit de appeler save() avec un objet ayant un id existant
    // mais on peut aussi vérifier si l'objet existe avant de le mettre à jour
    @Transactional
    public Optional<DirectorRecord> updateDirector(DirectorRecord directorRecord){
        Optional<Director> existingDirectorOpt = repositoryDirector.findById(directorRecord.id());
        if(existingDirectorOpt.isPresent()){
            // update les champs de l'objet existant avec les valeurs du nouvel objet
            mapperDirector.updateEntityFromRecord(directorRecord, existingDirectorOpt.get());
            Director dEntity = repositoryDirector.save(existingDirectorOpt.get());
            return Optional.of(mapperDirector.toRecord(dEntity));
        }
        return Optional.empty();
    }

    //delete : on peut utiliser deleteById() du repository, mais on peut aussi vérifier si l'objet existe avant de le supprimer
    // La méthode retourne true si la suppression a réussi, false sinon
    @Transactional
    public boolean deleteDirectorById(Long id){
        Optional<DirectorRecord> existingDirectorOpt = findDirectorByIdOptional(id);
        if(existingDirectorOpt.isPresent()){
            logger.info("Deleting director with id : {}", id);
            repositoryDirector.deleteById(id);
            return true;
        }
        return false;
    }

    //findAll : retourne une liste de tous les directeurs sous forme de Records par exemple
    public List<DirectorRecord> findAllDirectors(){
        List<DirectorRecord> directorRecords = new ArrayList<>();
        List<Director> directors = repositoryDirector.findAll();
        directors.forEach(director -> directorRecords.add(mapperDirector.toRecord(director)));
        return directorRecords;
    }

    //Methode liée aux relations : par exemple rajouter un film à un directeur
    @Transactional
    public MovieRecord addFilmToDirector(Long directorId, MovieRecord movieRecord) throws Exception {
        Optional<Director> d = repositoryDirector.findById(directorId);
        if(d.isPresent()){
            //à tester : bidrectionnalité...
            MovieRecord mRecord = this.serviceMovie.addMovie(movieRecord);
            Movie m = this.mapperMovie.fromRecordToEntity(mRecord);
            d.get().getMoviesDirected().add(m);
            m.setDirector(d.get());
            this.repositoryDirector.save(d.get());
            return mapperMovie.toRecord(m);
        }
        else{
            throw new Exception();
        }
    }






}
