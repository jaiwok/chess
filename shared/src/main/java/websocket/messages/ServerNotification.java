package websocket.messages;

public class ServerNotification extends ServerMessage{
    private String message;

    public ServerNotification(ServerMessageType type, String message) {
        super(type);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public ServerMessageType getMessageType(){
        return this.serverMessageType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageType(ServerMessageType type) {
        this.serverMessageType = type;
    }

}


