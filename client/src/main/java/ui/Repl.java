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
//        this.scanner = new Scanner(System.in);
    }

    public void run() throws IOException, URISyntaxException {
//        System.out.println("Welcome to Chess. Type 'help' for a list of commands.");

        UserContext userContext = UserContext.getInstance();
        UserInterface currentRepl = new PreLoginRepl(serverUrl, State.LOGGEDOUT, userContext);
        boolean firstTimeLogin = true;

        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {
            printPrompt(currentRepl.getState());
            String line = scanner.nextLine();

            try {

                result = currentRepl.eval(line);
                System.out.print(result);

                if(line.equals("quit")) {
                    break;
                }
                // check if we need to change uis
                switch (currentRepl.getState()){
                    case LOGGEDOUT -> currentRepl = new PreLoginRepl(serverUrl, State.LOGGEDOUT, userContext);
                    case LOGGEDIN -> {
                        currentRepl = new PostLoginRepl(serverUrl, State.LOGGEDIN, userContext);
                        fillMap(firstTimeLogin);
                        firstTimeLogin = false;
//                        System.out.print("LOGGED IN");

                    }
//                    case INGAME -> currentRepl = new GamePlayUi(serverUrl, State.INGAME, userContext);
                    case INGAME -> System.out.print("IN GAME");
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
            case LOGGEDOUT -> System.out.print("\n" + SET_TEXT_COLOR_BLUE + "Chess Login >>> " + RESET_TEXT_COLOR);
            case LOGGEDIN -> System.out.print("\n" + SET_TEXT_COLOR_BLUE + "Chess >>> "+ RESET_TEXT_COLOR);
            case INGAME -> System.out.print("\n" + SET_TEXT_COLOR_BLUE + "Chess Game >>> "+ RESET_TEXT_COLOR);
        }
    }

    private void fillMap(boolean firstTimeLogin) throws Exception {
        if(firstTimeLogin){
            serverFacade.fillMap();
        }
    }
}