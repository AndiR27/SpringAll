package ch.springall.controller;

import ch.springall.dtos.StudioRecord;
import ch.springall.service.ServiceStudio;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/studios", produces = "application/json")
public class ControllerStudio {

    private final ServiceStudio serviceStudio;

    public ControllerStudio(ServiceStudio serviceStudio) {
        this.serviceStudio = serviceStudio;
    }


    @GetMapping("/{id}/studio")
    public StudioRecord getStudio(@PathVariable("id") Long idStudio){
        return serviceStudio.findStudio(idStudio);
    }
}

