package websocket.messages;

import chess.ChessGame;

public class ServerLoadGame extends ServerMessage {

    private ChessGame game;

    public ServerLoadGame(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

}
