package ch.springall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.nio.file.AccessDeniedException;

/**
 * Classe centralisant la gestion des exceptions dans l'application.
 * Utilise le format "Problem Details" (RFC 7807) introduit dans Spring Boot 3.
 * L'annotation @RestControllerAdvice permet de capturer les exceptions
 * lancées par les contrôleurs REST et de les gérer de manière centralisée.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Exemple de gestion d'une exception métier personnalisée.
     * Retourne un ProblemDetail avec un message clair et un code HTTP cohérent.
     * ProblemDetail : instance standardisée pour représenter les détails d'un problème HTTP.
     * Elle contient des informations comme le type, le titre, le statut, le détail et l'instance du problème.
     * Ici, on gère une exception ResourceNotFoundException en renvoyant un statut 404 Not Found
     * et un message d'erreur explicite
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Ressource introuvable");
        problem.setType(URI.create("https://api.example.com/errors/not-found"));
        return problem;
    }

    /**
     * Gestion des erreurs de validation (Bean Validation).
     * Regroupe les messages d'erreur pour les champs invalides.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Erreur de validation");
        problem.setType(URI.create("https://api.example.com/errors/validation"));
        problem.setDetail("Un ou plusieurs champs sont invalides");

        // Exemple : on pourrait ajouter une extension "errors" contenant la liste des erreurs de champ
        problem.setProperty("errors", ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList());

        return problem;
    }

    /**
     * Gestion d'une exception technique générique non prévue explicitement.
     * Évite d'exposer la stacktrace et fournit une erreur 500 standardisée.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException() {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Erreur interne du serveur");
        problem.setType(URI.create("https://api.example.com/errors/internal"));
        problem.setDetail("Une erreur inattendue est survenue. Veuillez réessayer plus tard.");
        return problem;
    }

    /**
     * Gestion des erreurs d'accès non autorisé (403 Forbidden).
     * Utile pour les contrôles d'accès basés sur les rôles ou permissions.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Accès interdit");
        problem.setType(URI.create("https://api.example.com/errors/forbidden"));
        problem.setDetail("Vous n’êtes pas autorisé à accéder à cette ressource.");
        return problem;
    }

    /**
     * Gestion des erreurs d'authentification OAuth2.
     * Retourne un statut 401 Unauthorized avec un message adapté.
     */
    @ExceptionHandler(OAuth2AuthenticationException.class)
    public ProblemDetail handleOAuth2(OAuth2AuthenticationException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("Authentification requise");
        problem.setType(URI.create("https://api.example.com/errors/unauthorized"));
        problem.setDetail("Authentification OAuth2 invalide ou expirée.");
        // Optionnel : surface le code d’erreur OAuth2 (non sensible)
        problem.setProperty("error", ex.getError().getErrorCode());
        return problem;
    }
}
