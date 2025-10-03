package ch.springall.mapper;

import ch.springall.dtos.DirectorDTO;
import ch.springall.dtos.DirectorRecord;
import ch.springall.entity.Director;
import org.mapstruct.*;

//L'entité Director gère la relation avec Movie, on va donc utiliser le mapping de Movie pour éviter les cycles infinis
// Pour cela, on rajoute un paramètre pour @Mapper qui est "uses = MapperMovie.class" et on mappe la liste des films réalisés

@Mapper(componentModel = "spring", uses = MapperMovie.class)
public interface MapperDirector {

    @Mapping(target = "moviesDirected", source = "moviesDirected")
    DirectorDTO toDto(Director director);

    @InheritInverseConfiguration
    Director fromDtoToEntity(DirectorDTO directorDTO);

    // Pour les records :
    @Mapping(target = "moviesRecord", source = "moviesDirected")
    DirectorRecord toRecord(Director director);

    @InheritInverseConfiguration
    Director fromRecordToEntity(DirectorRecord directorDTO);

    //On peut utiliser des expressions pour des mappings plus complexes, par exemple pour calculer l'âge du directeur,
    // ou pour formater une date de naissance dans un format spécifique

    //On peut aussi définir des contraintes après le mapping avec des annotations de validation,
    // par exemple pour vérifier que la relation avec les films est cohérente avec l'annotation @AfterMapping
    @AfterMapping
    default void linkMovies(@MappingTarget Director entity) {
        if (entity.getMoviesDirected() != null) {
            entity.getMoviesDirected().forEach(m -> m.setDirector(entity));
        }
    }

    //On peut aussi définir un mapping pour une mise à jour partielle d'une entité existante avec @MappingTarget
    void updateEntityFromRecord(DirectorRecord dRecord, @MappingTarget Director entity);


}
