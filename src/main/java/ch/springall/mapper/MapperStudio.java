package ch.springall.mapper;

import ch.springall.dtos.StudioRecord;
import ch.springall.entity.Studio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {})
public interface MapperStudio {


    StudioRecord toRecord(Studio studio);

    Studio fromRecordToEntity(StudioRecord studioRecord);
}
