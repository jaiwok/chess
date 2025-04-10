package ui;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;

import serverdata.Facade;
import serverdata.UserContext;

import static ui.EscapeSequences.*;

public class Repl {
    String serverUrl;
    private final Facade serverFacade;

    public Repl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.serverFacade = new Facade(serverUrl);
    }

    public void run() throws IOException, URISyntaxException {

        UserContext userContext = UserContext.getInstance();
        UserInterface currentRepl = new PreLoginRepl(serverUrl, State.LOGGEDOUT, userContext);
        boolean firstLogin = true;

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {

            printPrompt(currentRepl.getState());
            String line = scanner.nextLine();

            try {
                result = currentRepl.evalCMD(line);
                System.out.print(result);

                if(line.equals("quit")) {
                    break;
                }

                switch (currentRepl.getState()){
                    case LOGGEDOUT -> currentRepl = new PreLoginRepl(serverUrl, State.LOGGEDOUT, userContext);
                    case LOGGEDIN -> {
                        currentRepl = new PostLoginRepl(serverUrl, State.LOGGEDIN, userContext);
                        createMap(firstLogin);
                        firstLogin = false;
                        }
                    case INGAME -> currentRepl = new InGameRepl(serverUrl, State.INGAME, userContext);
                }
            } catch (Throwable e) {

                var msg = e.toString();
                System.out.print(msg);
            }
        }

        System.out.println();
    }

    private static void printPrompt(State state) {
        switch (state) {
            case LOGGEDOUT -> System.out.print(SET_TEXT_COLOR_BLUE + "Chess Login >>> " + RESET_TEXT_COLOR);
            case LOGGEDIN -> System.out.print(SET_TEXT_COLOR_BLUE + "Chess >>> "+ RESET_TEXT_COLOR);
            case INGAME -> System.out.print(SET_TEXT_COLOR_BLUE + "Chess Game >>> "+ RESET_TEXT_COLOR);
        }
    }

    private void createMap(boolean firstLogin) throws Exception {
        if(firstLogin){ serverFacade.generateGameListMap(); }
    }
}