package ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import chess.ChessGame;
import serverdata.UserContext;

public class InGameRepl extends UserInterface{

    public InGameRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);
    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n" +
                SET_TEXT_BOLD + "  redraw" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to reload the board]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  leave" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to leave the game]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  move <start_location> <end_location>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to make a move]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  resign" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to forfeit the game]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  highlight <location>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to show legal moves]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  help" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [list available commands]\n" + RESET_TEXT_ITALIC + """
        """;
    }

    public String evalCMD(String input){
        try{
            var in = input.toLowerCase().split(" ");
            var cmd = (in.length > 0) ? in[0] : " ";
            var params = Arrays.copyOfRange(in, 1, in.length);

            return switch (cmd){
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String redraw(){
        ChessGame game = userContext.getGame();
        if(game == null){ // first time drawing
            game = new ChessGame();
        }
        PrintBoard.print(game, userContext.getColor());
        return "";
    }

    private String leave() throws IOException {
        if(userContext.isObserver()){
            setState(State.LOGGEDIN);
            return "Observer left the game";
        }

        userContext.setGame(null);
        userContext.setColor(null);
        setState(State.LOGGEDIN);
        return "Player Left Game";
    }

    private String move(String[] params){

        if(userContext.isObserver()){
            return (SET_TEXT_COLOR_RED + "You are an observer only, no cheating" + RESET_TEXT_COLOR);
        }

        return "Made Move";
    }

    private String resign() throws IOException {

        if(userContext.isObserver()){
            return (SET_TEXT_COLOR_RED + "You are an observer only, there is nothing to forfeit" + RESET_TEXT_COLOR);
        }

        System.out.println("Are you sure you wish to forfeit. Type 'Yes' to confirm");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        if(input.equals("Yes")){
            userContext.setGame(null);
            userContext.setColor(null);
            setState(State.LOGGEDIN);
            return "User Forfeited";
        } else{
            return "Forfeit canceled";
        }
    }

    private String highlight(String[] params){
        return "Highlighted Square";
    }
}
