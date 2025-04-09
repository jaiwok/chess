package ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

import com.google.gson.Gson;
import serverdata.UserContext;
import chess.ChessGame;
import model.GameData;
import model.returnobjects.*;
import websocket.commands.UserGameCommand;

import static ui.EscapeSequences.*;

public class PostLoginRepl extends UserInterface{
    Gson gson = new Gson();

    public PostLoginRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);
    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n" +
                SET_TEXT_BOLD + "  list" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [see all games on the server]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA +
                "  create <game name>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [create a new game]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA +
                "  play <game #> <White/Black>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [join a game as the selected color]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA +
                "  observe <game #>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [observe a game]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  logout" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [log out from your account]\n" +
                RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  quit" + RESET_TEXT_BOLD_FAINT +
                SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [exit the application]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD +
                SET_TEXT_COLOR_MAGENTA + "  help" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [list available commands]\n" + RESET_TEXT_ITALIC + """
                """;
    }

    public String evalCMD(String input){
        try{
            var in = input.toLowerCase().split(" ");
            var cmd = (in.length > 0) ? in[0] : "help";
            var params = Arrays.copyOfRange(in, 1, in.length);

            return switch (cmd){
                case "create" -> create(params);
                case "list" -> list();
                case "play" -> play(params);
                case "logout" -> logout();
                case "observe" -> observe(params);
                case "quit" -> "exiting...";
                default -> help();
            };
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String create(String[] params) throws Exception {
        if(params.length != 1){
            throw new Exception(SET_TEXT_COLOR_RED + "Expected <game id>\n"  + RESET_TEXT_COLOR);
        } else{
            GameData game = new GameData(0, null, null, params[0], new ChessGame());
            server.createGame(game);
            return "Created Game " + ": " + params[0] + "\n";
        }
    }

    private String list() throws Exception {
        GameList gameList = server.listGames();
        StringBuilder string = new StringBuilder();
//        string.append("\n");
        for (GameData game : gameList.games()) {
            int gameNum =  server.getGameNum(game.gameID());
            string.append(SET_TEXT_COLOR_BLUE)
                    .append(" Game ID ")
                    .append(SET_TEXT_COLOR_GREEN)
                    .append(gameNum)
                    .append(SET_TEXT_COLOR_BLUE)
                    .append(": ")
                    .append(RESET_TEXT_COLOR)
                    .append(game.gameName())
                    .append(" [White: ")
                    .append(game.whiteUsername())
                    .append(", Black: ")
                    .append(game.blackUsername())
                    .append("]\n");
        }
        return string.toString();
    }

    private String play(String[] params) throws Exception {

        boolean isUserInGame = false;
        
        if(params.length != 2){
            throw new Exception(SET_TEXT_COLOR_RED + "Expected format: <game #> <White/Black>\n" + RESET_TEXT_COLOR);
        } else if (!(params[0].matches("-?\\d+(\\.\\d+)?"))) {

            throw new Exception(SET_TEXT_COLOR_RED + "Expected number for game #\n" + RESET_TEXT_COLOR);
        } else if (!(Objects.equals(params[1], "white")) && !(Objects.equals(params[1], "black"))) {
            System.out.println(params[1]);
            throw new Exception(SET_TEXT_COLOR_RED + "Expected color white or black\n" + RESET_TEXT_COLOR);
        }else {
            int gameNum = Integer.parseInt(params[0]);
            int id = server.getGameId(gameNum);
            ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            JoinGameRequest joinParams = new JoinGameRequest(color, id);
            try{
                server.joinGame(joinParams);
            }catch (Exception e){
                if (Objects.equals(e.getMessage(), "403")){
                    return (SET_TEXT_COLOR_RED + "Color is already taken\n" + RESET_TEXT_COLOR);
                }
                if (Objects.equals(e.getMessage(), "888")){
                    System.out.println(SET_TEXT_COLOR_RED + "You are already in the game\n" + RESET_TEXT_COLOR);
                    isUserInGame = true;
                }
            }

            if (!isUserInGame) {
                userContext.setColor(color);
            }else{
                color = userContext.getColor();
            }

            userContext.setObserver(false);
            userContext.setColor(color);
            userContext.setGameId(id);

            //start up connection
            userContext.startWSConnection(serverUrl);
            //send observe command via wb
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userContext.getAuthToken(), userContext.getGameId(), null);
            String s = gson.toJson(cmd);
            userContext.wsClient.send(s);

            setState(State.INGAME);
            return "Joined Game as " + color +"\n";
        }
    }

    private String observe(String[] params) throws Exception {
        if(params.length != 1){
            throw new Exception(SET_TEXT_COLOR_RED + "Expected format: <game #>\n" + RESET_TEXT_COLOR);
        }  else if (!(params[0].matches("-?\\d+(\\.\\d+)?"))) {
            throw new Exception(SET_TEXT_COLOR_RED + "Expected number for game #\n" + RESET_TEXT_COLOR);
        }else {
            int gameNum = Integer.parseInt(params[0]);
            userContext.setGameId(server.getGameId(gameNum));
            userContext.setColor(ChessGame.TeamColor.WHITE);
            //start up connection
            userContext.startWSConnection(serverUrl);
            //send observe command via wb
            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.CONNECT, userContext.getAuthToken(), userContext.getGameId(), null);
            String s = gson.toJson(cmd);
            userContext.wsClient.send(s);

            userContext.setObserver(true);
            setState(State.INGAME);

            return "";
        }
    }

    private String logout() throws Exception {
        server.logout();
        setState(State.LOGGEDOUT);
        return "Logged out\n";
    }
}
