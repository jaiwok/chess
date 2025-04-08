package websocket.messages;

public class ServerError extends ServerMessage{
    private String errMsg;

    public ServerError(ServerMessageType type, String errMsg) {
        super(type);
        this.errMsg = errMsg;
    }

    public String getErrorMessage() {
        return errMsg;
    }

    public ServerMessage.ServerMessageType getMessageType(){
        return serverMessageType;
    }
}
