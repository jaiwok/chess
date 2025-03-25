package ui;

import model.UserData;
import model.returnobjects.AuthTokenResponse;
import serverdata.UserContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static ui.EscapeSequences.*;

public class PreLoginRepl extends UserInterface {

    public PreLoginRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);
    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n"
        + SET_TEXT_BOLD + "  register <username> <password> <email>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR + " [create a new account]\n" + RESET_TEXT_ITALIC
        + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  login <username> <password>" +  RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +" [login to an existing account]\n" + RESET_TEXT_ITALIC
        + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  quit" +  RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +  RESET_TEXT_COLOR +" [closes client]\n" + RESET_TEXT_ITALIC
        + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  help" +  RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +  RESET_TEXT_COLOR +" [list available commands]\n" + RESET_TEXT_ITALIC + """ 
        """;
    }

    public String eval(String input){
        try{
            var in = input.toLowerCase().split(" ");
            var cmd = (in.length > 0) ? in[0] : "help";
            var params = Arrays.copyOfRange(in, 1, in.length);

            return switch (cmd){
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "exiting...";
                default -> help();
            };
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String register(String[] params) throws Exception {
        if(params.length != 3) {
            throw new Exception("Expected: <username> <password> <email>");
        } else{
            UserData user = new UserData(params[0], params[1], params[2]);
            AuthTokenResponse authToken = server.register(user);
            setState(State.LOGGEDIN);
            return "registered!";
        }
    }

    private String login(String[] params) throws Exception {
        if(params.length != 2) {
            throw new Exception("Expected: <username> <password>");
        } else {
            UserData user = new UserData(params[0], params[1], null);
            AuthTokenResponse authToken = server.login(user);
            setState(State.LOGGEDIN);
            return "logged in as " + params[0];
        }
    }

}
