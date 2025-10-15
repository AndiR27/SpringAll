package ch.springall.exceptions;

/**
 * Exception levée lorsqu'une ressource n'est pas trouvée.
 * Sert à indiquer un 404 cohérent côté API.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
