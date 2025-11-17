package ch.springall.testServices;

import ch.springall.dtos.StudioRecord;
import ch.springall.entity.Studio;
import ch.springall.exceptions.ResourceNotFoundException;
import ch.springall.mapper.MapperStudio;
import ch.springall.service.ServiceStudio;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TestServiceStudio {

    @Autowired
    private ServiceStudio serviceStudio;

    @Autowired
    private MapperStudio mapperStudio;

    //test find the studio
    @Test
    void testAddStudio(){
        Studio s = this.getStudioHelper();
        StudioRecord sRecord = mapperStudio.toRecord(s);
        StudioRecord recordAdded = serviceStudio.addStudio(sRecord);

        assertNotNull(recordAdded);
        assertEquals(1L, recordAdded.id());

    }

    @Test
    void testFindStudio(){
        Studio s = this.getStudioHelper();
        StudioRecord sRecord = mapperStudio.toRecord(s);
        StudioRecord recordAdded = serviceStudio.addStudio(sRecord);

        assertNotNull(recordAdded);

        //find studio
        StudioRecord found = serviceStudio.findStudio(recordAdded.id());
        assertNotNull(found);
        assertEquals(recordAdded.id(), found.id());

    }

    @Test
    void testFindException() throws ResourceNotFoundException {
        assertThrows(ResourceNotFoundException.class, () -> serviceStudio.findStudio(101L));
    }

    private Studio getStudioHelper(){
        Studio studio = new Studio();
        studio.setStudioName("Warner Bros");
        studio.setStudioFoundedYear(1990);
        return studio;
    }
}
