package service.exceptions;

public class FaultyRequestException extends Exception{
    /**
     * 400
     *
     * @param message
     */
    public FaultyRequestException(String message) {
        super(message);
    }
}
