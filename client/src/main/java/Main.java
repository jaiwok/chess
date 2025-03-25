import chess.*;
import ui.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("♕ Chess Client: ♔");

        var serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        new Repl(serverUrl).run();
    }
}