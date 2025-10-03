package ch.springall.testServices;

// Mise en place d'une classe de test pour le service Director
// On va utiliser Junit5 et Mockito pour les tests unitaires
// Le but est de tester les méthodes du service DirectorService

import ch.springall.dtos.DirectorRecord;
import ch.springall.dtos.MovieRecord;
import ch.springall.entity.Director;
import ch.springall.entity.Genre;
import ch.springall.entity.Movie;
import ch.springall.mapper.MapperDirector;
import ch.springall.repository.jpa.RepositoryDirector;
import ch.springall.service.ServiceDirector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Les annotations :
// @SpringBootTest : pour charger le contexte Spring et permettre l'injection des dépendances
// @ExtendWith(MockitoExtension.class) : pour activer le support de Mockito dans les tests JUnit5
@SpringBootTest
public class TestServiceDirector {

    //On va récupérer le service DirectorService avec @Autowired
    //et le repository avec @MockitoBean pour simuler son comportement

    //service
    // L'utilisation de @Autowired dans les tests est acceptable pour injecter des dépendances réelles
    // car cela permet de tester le comportement réel du service avec ses dépendances
    @Autowired
    private ServiceDirector serviceDirector;

    //repository
    //On teste le service, pas le repository, donc on simule le repository avec @MockitoBean
    //qui crée un mock du repository et l'injecte dans le service
    @MockitoBean
    private RepositoryDirector repositoryDirector;

    //Pour le mapper, on peut soit l'injecter avec @Autowired (si on veut tester le mapping réel)
    //soit le mocker avec @MockBean (si on veut simuler son comportement)
    @Autowired
    private MapperDirector mapperDirector;

    //Pour définir un test : annotation @Test et une méthode publique sans paramètre
    // Mise en place de AAA (Arrange, Act, Assert) pour structurer les tests
    @Test
    @DisplayName("Test ajout d'un directeur")
    public void testAddDirector(){
        //Arrange : préparation des données et du contexte
        DirectorRecord d = new DirectorRecord(null, "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 2, null);
        Director dEntity = mapperDirector.fromRecordToEntity(d);
        dEntity.setId(1L);
        when(repositoryDirector.save(any(Director.class))).thenReturn(dEntity);
        //Act : appel de la méthode à tester
        DirectorRecord result = serviceDirector.addDirectorRecord(d);
        //Assert : vérification du résultat avec des assertions
        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(repositoryDirector).save(any(Director.class));

    }

    //Test pour vérifier si une personne existe après avoir été rajouté
    @Test
    public void testFindDirector(){
        DirectorRecord d = new DirectorRecord(null, "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 2, null);
        Director dEntity = mapperDirector.fromRecordToEntity(d);
        dEntity.setId(1L);
        when(repositoryDirector.save(any(Director.class))).thenReturn(dEntity);
        when(repositoryDirector.findById(1L)).thenReturn(Optional.of(dEntity));

        // save et find
        serviceDirector.addDirectorRecord(d);
        Optional<DirectorRecord> result = serviceDirector.findDirectorByIdOptional(1L);

        //tests
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        verify(repositoryDirector).save(any(Director.class));
        verify(repositoryDirector).findById(1L);
    }

    //test pour un Update
    @Test
    public void testUpdateDirector(){
        //Arrange
        DirectorRecord d = new DirectorRecord(null, "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 2, null);
        Director dEntity = mapperDirector.fromRecordToEntity(d);
        dEntity.setId(1L);
        when(repositoryDirector.save(any(Director.class))).thenReturn(dEntity);
        when(repositoryDirector.findById(1L)).thenReturn(Optional.of(dEntity));

        //Act : update
        DirectorRecord dAdded = serviceDirector.addDirectorRecord(d);
        DirectorRecord dUpdate = new DirectorRecord(dAdded.id(), "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 3, null);

        Optional<DirectorRecord> result = serviceDirector.updateDirector(dUpdate);

        //Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().id());
        assertEquals(3, result.get().oscarCount());
        verify(repositoryDirector, Mockito.times(2)).save(any(Director.class));
        //Le save est appelé 2 fois : une fois pour l'ajout, une fois pour la mise à jour
        verify(repositoryDirector).findById(1L);
    }

    //test pour un delete
    @Test
    @DisplayName("Test suppression d'un directeur")
    public void testDeleteDirector(){
        //Arrange
        DirectorRecord d = new DirectorRecord(null, "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 2, null);
        Director dEntity = mapperDirector.fromRecordToEntity(d);
        dEntity.setId(1L);
        when(repositoryDirector.save(any(Director.class))).thenReturn(dEntity);
        when(repositoryDirector.findById(1L)).thenReturn(Optional.of(dEntity));
        Mockito.doNothing().when(repositoryDirector).deleteById(1L);

        //Act : save then delete
        DirectorRecord dAdded = serviceDirector.addDirectorRecord(d);
        boolean deleteResult = serviceDirector.deleteDirectorById(dAdded.id());
        when(repositoryDirector.findById(1L)).thenReturn(Optional.of(dEntity));
        //vérification que le directeur n'existe plus
        Optional<DirectorRecord> findResult = serviceDirector.findDirectorByIdOptional(1L);

        //Assert
        assertTrue(deleteResult);
        assertNotNull(findResult);
        assertFalse(findResult.isEmpty());
        verify(repositoryDirector).save(any(Director.class));
        verify(repositoryDirector).deleteById(1L);

    }

    //test des relations et propagation des opérations (cascade)
    @Test
    public void testAddDirectorWithMovies() throws Exception {
        //Arrange
        MovieRecord m1 = new MovieRecord(null, "Pulp Fiction", LocalDateTime.of(1998, Month.APRIL, 15, 12, 12), Genre.THRILLER, 9.5, null);
        //Movie m2 = new Movie("Kill Bill", LocalDateTime.of(10, Month.APRIL, 2003, 12, 12), Genre.ACTION, 8.5, null);
        DirectorRecord dRecordNew = new DirectorRecord(null, "Quentin", "Tarantino",
                LocalDate.of(1963, 3, 27), 2, new ArrayList<>());
        Director d = mapperDirector.fromRecordToEntity(dRecordNew);
        d.setId(1L);
        when(repositoryDirector.save(any(Director.class))).thenReturn(d);
        when(repositoryDirector.findById(any(Long.class))).thenReturn(Optional.of(d));

        //Act
        DirectorRecord dRecord = serviceDirector.addDirectorRecord(mapperDirector.toRecord(d));
        MovieRecord movieAddedRecord = serviceDirector.addFilmToDirector(dRecord.id(), m1);

        //Assert
        assertNotNull(dRecord);
        assertNotNull(movieAddedRecord);
        assertEquals("Pulp Fiction", movieAddedRecord.title());
        assertEquals(d.getId(), movieAddedRecord.directorId());
        verify(repositoryDirector, Mockito.times(2)).save(any(Director.class));
        verify(repositoryDirector).findById(any(Long.class));
    }

}
