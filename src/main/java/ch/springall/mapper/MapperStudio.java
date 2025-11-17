package ch.springall.mapper;

import ch.springall.dtos.StudioRecord;
import ch.springall.entity.Studio;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MapperDirector.class)
public interface MapperStudio {

    @Mapping(source = "directorList", target = "directorList")
    StudioRecord toRecord(Studio studio);

    @InheritInverseConfiguration
    Studio fromRecordToEntity(StudioRecord studioRecord);
}
