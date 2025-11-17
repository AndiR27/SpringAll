package ch.springall.dtos;


import ch.springall.entity.Director;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record StudioRecord(
        Long id,
        @NotBlank
        String studioName,
        int studioFoundedYear,
        List<DirectorRecord> directorList
) {
}
