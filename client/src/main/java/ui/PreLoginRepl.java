package ui;

import model.UserData;
import model.returnobjects.AuthTokenResponse;
import serverdata.UserContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class PreLoginRepl extends UserInterface {

    public PreLoginRepl(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        super(serverUrl, state, userContext);
    }

    public String help(){
        return """
        """ + SET_TEXT_COLOR_MAGENTA + "Available commands:\n" + SET_TEXT_BOLD +
        "  register <username> <password> <email>" + RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
        " [create a new account]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA +
        "  login <username> <password>" +  RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC + RESET_TEXT_COLOR +
        " [login to an existing account]\n" + RESET_TEXT_ITALIC + SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  quit" +
        RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +  RESET_TEXT_COLOR +" [closes client]\n" + RESET_TEXT_ITALIC +
        SET_TEXT_BOLD + SET_TEXT_COLOR_MAGENTA + "  help" +  RESET_TEXT_BOLD_FAINT + SET_TEXT_ITALIC +
        RESET_TEXT_COLOR +" [list available commands]\n" + RESET_TEXT_ITALIC + """ 
        """;
    }

    public String evalCMD(String input){
        try{

            var in = input.toLowerCase().split(" ");
            var cmd = (in.length > 0) ? in[0] : "";
            var params = Arrays.copyOfRange(in, 1, in.length);

            return switch (cmd){
                case "clear" -> clear(params); //Hidden command
                case "quit" -> "exiting...";
                case "register" -> register(params);
                case "login" -> login(params);
                default -> help();
            };
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String clear(String[] params) throws Exception{
        if(params.length !=1){
            throw new Exception( SET_TEXT_COLOR_RED + "Expected: admin password" + RESET_TEXT_COLOR);
        } else if (!Objects.equals(params[0], "poopypants")){
            throw new Exception( SET_TEXT_COLOR_RED + "Invalid admin password" + RESET_TEXT_COLOR);
        }else {
            return server.clearDB();
        }
    }

    private String register(String[] params) throws Exception {
        if(params.length != 3) {
            throw new Exception( SET_TEXT_COLOR_RED + "Expected format: <username> <password> <email>" + RESET_TEXT_COLOR);
        } else {
            UserData user = new UserData(params[0], params[1], params[2]);
            try {
                server.register(user);
                setState(State.LOGGEDIN);
                return "registered user " + params[0] + " and logged in";
            } catch (Exception e) {
                return SET_TEXT_COLOR_RED + "Username: " + RESET_TEXT_COLOR + params[0] + SET_TEXT_COLOR_RED + " already in use" + RESET_TEXT_COLOR;
            }
        }
    }

    private String login(String[] params) throws Exception {
        if(params.length != 2) {
            throw new Exception(SET_TEXT_COLOR_RED + "Expected format: <username> <password>"  + RESET_TEXT_COLOR);
        } else {
            try {
                UserData user = new UserData(params[0], params[1], null);
                AuthTokenResponse authToken = server.login(user);
                setState(State.LOGGEDIN);
                return "logged in as " + params[0];
            } catch (Exception e) {
                return SET_TEXT_COLOR_RED + "Incorrect Username or Password" + RESET_TEXT_COLOR;
            }
        }
    }

}
