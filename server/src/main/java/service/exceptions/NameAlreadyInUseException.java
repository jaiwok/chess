package service.exceptions;

public class NameAlreadyInUseException extends Exception {
    /**
     * 403
     *
     * @param message
     */
    public NameAlreadyInUseException(String message) {
        super(message);
    }
}