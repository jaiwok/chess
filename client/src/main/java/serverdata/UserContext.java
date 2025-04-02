package serverdata;

import chess.ChessGame;

public class UserContext {
    private static UserContext instance;
    private String authToken;
    private ChessGame game;
    private ChessGame.TeamColor color;
    private int gameId;
    private boolean observer;

    private UserContext() {}

    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public ChessGame.TeamColor getColor(){
        return this.color;
    }


    public ChessGame getGame(){ return this.game; }


    public String getAuthToken() {
        return authToken;
    }


    public int getGameId() {
        return gameId;
    }


    public void setColor(ChessGame.TeamColor color){
        this.color = color;
    }


    public void setGame(ChessGame game){
        this.game = game;
    }


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public boolean isObserver() { return observer; }

    public void setObserver(boolean observer) { this.observer = observer; }
}
