package websocket.messages;

public class ServerError extends ServerMessage{
    private String errorMessage;

    public ServerError(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ServerMessage.ServerMessageType getMessageType(){
        return serverMessageType;
    }
}
