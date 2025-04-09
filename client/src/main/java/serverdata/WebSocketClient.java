package serverdata;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ui.*;
import websocket.messages.*;
import websocket.messages.ServerMessage.ServerMessageType;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

import static ui.EscapeSequences.*;

@ClientEndpoint
public class WebSocketClient extends Endpoint{

    private Session session;
    private final UserContext userContext;
    private Gson gson = new Gson();

    public WebSocketClient(String uri, UserContext userContext) {
        this.userContext = userContext;
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            System.err.println("Attempted connection to : " + uri + " but failed\n");
            System.err.println("WebSocket connection error: " + e.getMessage()+"\n");
        }

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String s) {
                JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();

                String typeString = jsonObject.get("serverMessageType").getAsString();
                ServerMessage.ServerMessageType mType = ServerMessageType.valueOf(typeString);

                switch (mType){
                    case ServerMessageType.NOTIFICATION -> serverNotification(jsonObject);
                    case ServerMessageType.LOAD_GAME -> serverLoadGame(jsonObject);
                    case ServerMessageType.ERROR -> serverError(jsonObject);
                }
            }
        });
    }

    private void serverNotification(JsonObject obj){
        ServerNotification notification = gson.fromJson(obj, ServerNotification.class);
        String msg = notification.getMessage();
        System.out.println("\n   " + SET_TEXT_COLOR_GREEN + msg + RESET_TEXT_COLOR);
        System.out.print(SET_TEXT_COLOR_BLUE + "Chess Game >>> "+ RESET_TEXT_COLOR);
    }

    private  void serverLoadGame(JsonObject obj){
        ServerLoadGame loadGame = gson.fromJson(obj, ServerLoadGame.class);
        ChessGame game = loadGame.getGame();
        userContext.setGame(game);
        System.out.println("");
        PrintBoard.print(game, userContext.getColor());
        System.out.print(SET_TEXT_COLOR_BLUE + "Chess Game >>> "+ RESET_TEXT_COLOR);
    }

    private void serverError(JsonObject obj){
        ServerError error = gson.fromJson(obj, ServerError.class);
        String msg = error.getErrorMessage();
        System.out.println("\n" + SET_TEXT_COLOR_RED + msg + RESET_TEXT_COLOR);
        System.out.print(SET_TEXT_COLOR_BLUE + "Chess Game >>> "+ RESET_TEXT_COLOR);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
    }


    public void send(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        } else {
            System.err.println("WebSocket is not open.\n");
        }
    }

    public void close() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("WebSocket close error: " + e.getMessage() + "\n");
        }
    }


}