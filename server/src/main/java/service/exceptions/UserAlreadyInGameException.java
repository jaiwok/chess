package service.exceptions;

public class UserAlreadyInGameException extends Exception{
    /**
     * 888
     *
     * @param message
     */
    public UserAlreadyInGameException(String message) {
        super(message);
    }
}
