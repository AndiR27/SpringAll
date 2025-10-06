package ch.springall.controller;


//Le controller pour Director va gérer les requêtes HTTP liées aux réalisateurs
//Il va utiliser le service DirectorService pour la logique métier

// Pour chaque controller, on va définir une route de base, et différentes routes pour les autres endpoints
// Par exemple, pour Director, on peut définir la route de base "/directors"
// et des routes pour les opérations CRUD : GET /directors, GET /directors/{id}, POST /directors, PUT /directors/{id}, DELETE /directors/{id}

import ch.springall.dtos.DirectorDTO;
import ch.springall.dtos.DirectorRecord;
import ch.springall.service.ServiceDirector;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//L'annotation @RestController indique que cette classe est un controller REST
//Elle combine @Controller et @ResponseBody, ce qui signifie que les méthodes de cette classe
//retournent directement des objets qui seront convertis en JSON (ou XML) et envoyés dans la réponse HTTP

//L'annotation @ReuqestMapping : Route de base pour ce controller, on peut aussi définir des routes spécifiques pour chaque méthode
// on peut aussi définir le type de contenu produit (produces) et consommé (consumes) par ce controller (ex: application/json)
@RestController
@RequestMapping(path = "/directors", produces = "application/json")
public class ControllerDirector {

    // Injection du service DirectorService : on utilise l'injection par constructeur (la plus recommandée)
    private final ServiceDirector serviceDirector;

    public ControllerDirector(ServiceDirector serviceDirector) {
        this.serviceDirector = serviceDirector;
    }

    /**
     * Endpoints pour les opérations CRUD et autres opérations spécifiques
     * Pour chaque opération, on définit une méthode avec l'annotation appropriée
     * (ex: @GetMapping, @PostMapping, @PutMapping, @Delete)
     * et on utilise le service pour effectuer l'opération
     * Pour chaque méthode : on préfère renvoyer une ResponseEntity (ou Iterable pour les listes)
     */

    //------------------------------
    // 1 : Récupérer tous les directeurs (GET /directors)
    // Cette méthode va retourner tous les directeurs
    // On va préférer renvoyer une ResponseEntity pour gérer les codes de statut HTTP
    // La ResponseEntity va contenir la liste des directeurs et le code 200 OK
    //------------------------------
    @GetMapping
    public ResponseEntity<Iterable<DirectorRecord>> getAllDirectors() {
        List<DirectorRecord> directors = serviceDirector.findAllDirectors();
        if(directors.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content si la liste est vide
        }
        return ResponseEntity.ok(directors);
    }

    //------------------------------
    // 2: Récupérer un directeur par son ID (GET /directors/{id})
    //------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<DirectorRecord> getDirectorById(@PathVariable Long id){
        return serviceDirector.findDirectorByIdOptional(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //------------------------------
    // 3: Ajouter un nouveau directeur (POST /directors)
    // Pour un ajout : on suppose qu'on reçoit un objet JSON valide qu'on va récupérer en tant que record et ajouter
    // en base grâce au service
    //------------------------------
    @PostMapping(path = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DirectorRecord> addDirector(@Valid @RequestBody DirectorRecord dRecord){
        DirectorRecord dAdded = serviceDirector.addDirectorRecord(dRecord);
        return ResponseEntity.status(201).body(dAdded);
    }

    //----------------------
    // 4: Mettre à jour un directeur existant (PUT /directors/{id})
    // Pour une mise à jour, on utilise PUT et on envoie l'objet complet (remplacement)
    //----------------------
    @PutMapping(path = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity<DirectorRecord> updateDirector(@Valid @RequestBody DirectorRecord dRecord){
        Optional<DirectorRecord> updatedDirector = serviceDirector.updateDirector(dRecord);
        return updatedDirector.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //------------------------------------
    // 5: Supprimer un directeur existant (DEL /{id})
    // On retourne aucun contenu dans le cas d'un delete, mais on peut lancer
    // une erreur si l'id n'existe pas
    @DeleteMapping(path ="/{id}")
    public ResponseEntity<Boolean> deleteDirector(@PathVariable Long id){
        if(this.serviceDirector.deleteDirectorById(id)){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
