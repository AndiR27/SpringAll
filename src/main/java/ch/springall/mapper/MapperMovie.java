package ch.springall.mapper;


import ch.springall.dtos.MovieDTO;
import ch.springall.dtos.MovieRecord;
import ch.springall.entity.Movie;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// Le Mapper est une interface qui permet de mapper les objets entre eux : DTO <-> Entity
// On utilise l'annotation @Mapper de MapStruct pour indiquer que c'est un mapper
// et componentModel = "spring" pour que Spring puisse gérer l'instance du mapper
// de cette façon, on peut injecter le mapper dans les services ou les contrôleurs


@Mapper(componentModel = "spring")
public interface MapperMovie {

    //Dans un mapper : on définit des méthodes de mapping entre les objets source et cible (DTO et Entity)
    // Ici, on mappe un Movie vers un MovieDTO
    // On peut aussi définir des conversions personnalisées si nécessaire avec des annotations supplémentaires
    // telles que @Mapping, @Mappings, @InheritInverseConfiguration, etc.
    // Dans ce Mapping, la propriété directorId de MovieDTO est mappée à partir de director.id de Movie
    @Mapping(source = "director.id", target = "directorId")
    MovieDTO toDTO(Movie movie);

    //Lorsque l'on fait le mapping inverse (de DTO vers Entity), on peut utiliser l'annotation @InheritInverseConfiguration
    // pour réutiliser la configuration de mapping définie dans la méthode toDTO, sinon il faut redéfinir les mappings
    // avec @Mapping
    // Ici on va ignorer director (sera réattribué en Service avec un findById)
    @Mapping(target = "director", ignore = true)
    Movie fromDTOtoEntity(MovieDTO movieDTO);

    //On peut aussi définir le mapping des record : le principe est le même qu'avec un DTO classique
    @Mapping(target = "directorId", source = "director.id")
    MovieRecord toRecord(Movie movie);

    @InheritInverseConfiguration
    @Mapping(target = "director", ignore = true)
    Movie fromRecordToEntity(MovieRecord movieRecord);

}
