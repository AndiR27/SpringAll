package ch.springall.service;

import ch.springall.dtos.DirectorRecord;
import ch.springall.dtos.StudioRecord;
import ch.springall.entity.Director;
import ch.springall.entity.Studio;
import ch.springall.exceptions.ResourceNotFoundException;
import ch.springall.mapper.MapperStudio;
import ch.springall.repository.jpa.RepositoryDirector;
import ch.springall.repository.jpa.RepositoryStudio;
import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@Service
public class ServiceStudio {

    private final NativeWebRequest nativeWebRequest;
    private RepositoryStudio repositoryStudio;
    private MapperStudio mapperStudio;
    private RepositoryDirector repositoryDirector;

    public ServiceStudio(RepositoryStudio repositoryStudio, MapperStudio mapperStudio, NativeWebRequest nativeWebRequest) {
        this.repositoryStudio = repositoryStudio;
        this.mapperStudio = mapperStudio;
        this.nativeWebRequest = nativeWebRequest;
    }


    //add a studio
    public StudioRecord addStudio(StudioRecord studioRecord){
        if(repositoryStudio.findByStudioName(studioRecord.name()) != null){
            throw new EntityExistsException("Studio with name " + studioRecord.name() + " already exists");
        }
        Studio s = repositoryStudio.save(mapperStudio.fromRecordToEntity(studioRecord));

        return mapperStudio.toRecord(s);
    }

    //Find a studio
    public StudioRecord findStudio(Long studioId){
        Optional<Studio> studio = repositoryStudio.findById(studioId);
        if(studio.isEmpty()){
            throw new ResourceNotFoundException("Studio with id " + studioId + " not found");
        }
        return mapperStudio.toRecord(studio.get());
    }

    //Update a studio
    public StudioRecord updateStudio(StudioRecord studioRecord){
        Optional<Studio> existingStudioOpt = repositoryStudio.findById(studioRecord.id());
        if(existingStudioOpt.isEmpty()){
            throw new ResourceNotFoundException("Studio with id " + studioRecord.id() + " not found");
        }
        Studio updatedStudio = repositoryStudio.save(existingStudioOpt.get());
        return mapperStudio.toRecord(updatedStudio);
    }

    //delete a studio
    public void deleteStudio(Long studioId){
        if(repositoryStudio.findById(studioId).isPresent()){
            repositoryStudio.deleteById(studioId);
        }
    }

    public StudioRecord addDirector(Long studioId, Long directorId){
        Optional<Director> existingDirectorOpt = repositoryDirector.findById(directorId);
        Optional<Studio> existingStudioOpt = repositoryStudio.findById(studioId);
        if(existingStudioOpt.isEmpty() || existingDirectorOpt.isEmpty()){
            throw new ResourceNotFoundException("Director with id " + directorId + " not found");
        }
        existingStudioOpt.get().getDirectorList().add(existingDirectorOpt.get());
        Studio updatedStudio = repositoryStudio.save(existingStudioOpt.get());
        return mapperStudio.toRecord(updatedStudio);
    }
}
