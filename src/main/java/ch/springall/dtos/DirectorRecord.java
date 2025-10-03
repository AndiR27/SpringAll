package ch.springall.dtos;

// Un record est une forme spéciale de DTO qui est immuable et qui réduit le boilerplate code.
// Il est disponible à partir de Java 16. Un record est une classe qui est principalement utilisée pour transporter des données.
// Il est défini avec le mot-clé 'record' et ses composants sont définis dans la déclaration du record.

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record DirectorRecord(
        //Les attributs sont définis dans la déclaration du record et sont implicitement privés et finals
        Long id,
        //Un attribut dans notre record peut contenir des contraintes de validation comme @NotNull, @Size, etc.
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        // @JsonFormat permet de définir le format de la date lors de la sérialisation/désérialisation JSON
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate birthDate,
        int oscarCount,

        // Pour une relation one-to-many, on peut inclure une liste d'autres records pour
        // faire le mapping des entités associées
        List<MovieRecord> moviesRecord


) {
    public void updateFrom(DirectorRecord directorRecord) {

    }
}
