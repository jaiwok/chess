package ui;

import java.io.IOException;
import java.net.URISyntaxException;

import serverdata.*;

public abstract class UserInterface {
    protected State state = State.LOGGEDOUT;
    protected String serverUrl;
    protected Facade server;
    protected UserContext userContext;

    public UserInterface(String serverUrl, State state, UserContext userContext) throws IOException, URISyntaxException {
        this.state = state;
        this.serverUrl = serverUrl;
        this.server = new Facade(serverUrl);
        this.userContext = userContext;
        Repl repl = new Repl(serverUrl);
    }

    public abstract String help();

    public abstract String eval(String input);

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
