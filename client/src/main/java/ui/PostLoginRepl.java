package ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import serverdata.UserContext;
import chess.ChessGame;
import model.GameData;
import model.returnobjects.*;

import static ui.EscapeSequences.*;

public class PostLoginRepl extends UserInterface{


    public PostLoginRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);

    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n" +
                SET_TEXT_BOLD + "  list" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [see all games on the server]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  create <game name>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [create a new game]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  play <game ID> <BLACK/WHITE>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [join a game as the selected color]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  observe <game ID>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [observe a game]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  logout" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [log out from your account]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  quit" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [exit the application]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  help" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [list available commands]\n" + RESET_TEXT_ITALIC + """
                """;
            }

            public String eval(String input){
                try{
                    var in = input.toLowerCase().split(" ");
                    var cmd = (in.length > 0) ? in[0] : "help";
                    var params = Arrays.copyOfRange(in, 1, in.length);

                    return switch (cmd){
                        case "create" -> create(params);
                        case "list" -> list();
                        case "play" -> play(params);
                        case "observe" -> observe(params);
                        case "logout" -> logout(params);
                        case "quit" -> "exiting...";
                        default -> help();
                    };
                } catch (Exception e){
                    return e.getMessage();
                }
            }

            private String create(String[] params) throws Exception {
                if(params.length != 1){
                    throw new Exception(SET_TEXT_COLOR_RED + "Expected <game name>"  + RESET_TEXT_COLOR);
                } else{
                    GameData game = new GameData(0, null, null, params[0], new ChessGame());
                    server.createGame(game);
                    return "created game " + (server.nextGameNum - 1) + ": " + params[0];
                }
            }

            private String list() throws Exception {
                GameList gameList = server.listGames();
                StringBuilder string = new StringBuilder();

                for (GameData game : gameList.games()) {
                    int gameNum =  server.getGameNum(game.gameID());
                    string.append("Game: ")
                            .append(gameNum)
                            .append(" ")
                            .append(game.gameName())
                            .append(" - White: ")
                            .append(game.whiteUsername())
                            .append(", Black: ")
                            .append(game.blackUsername())
                            .append("\n");
                }

                return string.toString();

            }

            private String play(String[] params) throws Exception {
                if(params.length != 2){
                    throw new Exception(SET_TEXT_COLOR_RED + "Expected <game ID> <BLACK/WHITE>" + RESET_TEXT_COLOR);
                } else {
                    int gameNum = Integer.parseInt(params[0]);
                    int id = server.getGameId(gameNum);
                    ChessGame.TeamColor color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
                    JoinGameRequest joinParams = new JoinGameRequest(color, id);
                    server.joinGame(joinParams);
                    setState(State.INGAME);
                    userContext.setColor(color);

                    return "Joined Game as " + color;
                }
            }

            private String observe(String[] params) throws Exception {
                if(params.length != 1){
                    throw new Exception(SET_TEXT_COLOR_RED + "Expected <game ID>" + RESET_TEXT_COLOR);
                } else {
                    int gameNum = Integer.parseInt(params[0]);
                    int id = server.getGameId(gameNum);
                    setState(State.INGAME);

                    return "Watching game: (not implemented)";
                }
            }

            private String logout(String[] params) throws Exception {
                server.logout();
                setState(State.LOGGEDOUT);
                return "Signed out";
            }
        }
