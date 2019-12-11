package svc.exception;

/**
 * Gonna force 400 error.
 */
public class IncorrectRequestException extends RuntimeException {

    public IncorrectRequestException() {
    }

    public IncorrectRequestException(String message) {
        super(message);
    }
}
