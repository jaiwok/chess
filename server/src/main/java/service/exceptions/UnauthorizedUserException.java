package service.exceptions;

public class UnauthorizedUserException extends Exception{
    /**
     * 401
     *
     * @param message
     */
    public UnauthorizedUserException(String message) {
        super(message);
    }
}
