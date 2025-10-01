package ch.springall.dtos;

import ch.springall.entity.Genre;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;


public record MovieRecord(
        Long id,
        @NotBlank
        String title,
        @JsonFormat(pattern = "dd/MM/yyyy:HH:mm")
        LocalDateTime releaseDate,
        Genre genre,
        double rating,
        // Pour stocker la relation Many-to-One avec Director, on peut inclure l'ID du réalisateur
        // plutôt que l'objet Director complet pour éviter les cycles de sérialisation
        Long directorId
) {
}
