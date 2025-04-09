package ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import com.google.gson.Gson;
import serverdata.UserContext;
import serverdata.WebSocketClient;
import websocket.commands.UserGameCommand;

public class InGameRepl extends UserInterface{
    Gson gson = new Gson();

    public InGameRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);
    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n" +
                SET_TEXT_BOLD + "  redraw" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
                " [to reload the board]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA +
                "  leave" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to leave the game]\n" +
                RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  move <start_location> <end_location> <promotion piece>" +
                RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [to make a move]\n" + RESET_TEXT_ITALIC +
                SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  resign" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +
                RESET_TEXT_COLOR + " [to forfeit the game]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD +
                SET_TEXT_COLOR_MAGENTA + "  highlight <location>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +
                RESET_TEXT_COLOR + " [to show legal moves]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD +
                SET_TEXT_COLOR_MAGENTA + "  help" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +
                RESET_TEXT_COLOR + " [list available commands]\n" + RESET_TEXT_ITALIC + """
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

        //send observe command via wb
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, userContext.getAuthToken(), userContext.getGameId(), null);
        String s = gson.toJson(cmd);
        userContext.wsClient.send(s);
        userContext.closeWSConnection();

        userContext.setObserver(false);
        userContext.setGame(null);
        userContext.setGame(null);
        userContext.setColor(null);
        setState(State.LOGGEDIN);
        return "";
    }

    private String move(String[] params) throws Exception {

        if(params.length != 2){
            throw new Exception(SET_TEXT_COLOR_RED +
                    "Expected format: <start_location(Column + Row)> <end_location(Column + Row)> <promotion piece>\n"
                    + RESET_TEXT_COLOR);
        }

        ChessMove move = parseMove(params);

        //send observe command via wb
        UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, userContext.getAuthToken(), userContext.getGameId(), move);
        String s = gson.toJson(cmd);
        userContext.wsClient.send(s);

        return "";
    }

    private String resign() throws IOException {

//        Taken care of by Websocket
        if(userContext.isObserver()){
            return (SET_TEXT_COLOR_RED + "You are an observer only, there is nothing to forfeit" + RESET_TEXT_COLOR);
        }

        System.out.println("Are you sure you wish to forfeit. Type 'Yes' to confirm \n");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        if(input.equals("Yes")){

            UserGameCommand cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, userContext.getAuthToken(), userContext.getGameId(), null);
            String s = gson.toJson(cmd);
            userContext.wsClient.send(s);

            return "You Forfeited \n";
        } else{
            return "Forfeit canceled \n";
        }
    }

    private String highlight(String[] params) throws Exception {

        if (params.length < 1) {
            throw new Exception(SET_TEXT_COLOR_RED + "Invalid Highlight input. Expected at least 1 argument.\n" + RESET_TEXT_COLOR);
        }

        ChessPosition position = parsePosition(params[0]);
        Collection<ChessMove> moves = userContext.getGame().validMoves(position);
        if(moves == null){
            return "That spot is empty\n";
        }
        if(moves.isEmpty()){
            return "That piece can't move\n";
        }

        PrintBoard.printWithHighlights(userContext.getGame(), userContext.getColor(), moves);

        return "";
    }


    private ChessMove parseMove(String[] params) throws Exception {
        if (params.length < 2) {
            throw new Exception(SET_TEXT_COLOR_RED +
                    "Invalid move input. Expected at least 2 parameters for start or end location.\n" +
                    RESET_TEXT_COLOR);
        }

        ChessPosition start = parsePosition(params[0]); // e.g., "a2"
        ChessPosition end = parsePosition(params[1]);   // e.g., "a3"
        ChessPiece.PieceType promotion = null;

        // If promotion piece is provided (like "Q", "R", etc.)
        if (params.length == 3 && params[2] != null) {
            switch (params[2].toUpperCase()) {
                case "Q" -> promotion = ChessPiece.PieceType.QUEEN;
                case "R" -> promotion = ChessPiece.PieceType.ROOK;
                case "B" -> promotion = ChessPiece.PieceType.BISHOP;
                case "N" -> promotion = ChessPiece.PieceType.KNIGHT;
                default -> throw new Exception(SET_TEXT_COLOR_RED +
                        "Invalid promotion piece: " +
                        params[2] + 
                        " Expected Q, R, B, N \n" +
                        RESET_TEXT_COLOR);
            }
        }

        return new ChessMove(start, end, promotion);
    }

    private ChessPosition parsePosition(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception( SET_TEXT_COLOR_RED + "Invalid position: " + pos +"\n"+ RESET_TEXT_COLOR);
        }

        char file = Character.toLowerCase(pos.charAt(0)); // 'a' through 'h'
        int row = Character.getNumericValue(pos.charAt(1)); // 1 through 8

        int col = file - 'a' + 1;

        if (col < 1 || col > 8 || row < 1 || row > 8) {
            throw new Exception(SET_TEXT_COLOR_RED + "Position out of bounds: " + pos +"\n" +SET_TEXT_COLOR_RED);
        }

        return new ChessPosition(row, col);
    }
}
