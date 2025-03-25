package serverdata;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import model.*;
import model.returnobjects.*;

public class Facade{

    private String serverUrl;
    private static Map<Integer, Integer> gameNumId = new HashMap<>();
    public static int nextGameNum = 1;

    public Facade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getGameId(int gameNum){
        try {
            return gameNumId.get(gameNum);
        } catch(Exception e) {
            throw new RuntimeException("invalid game number");
        }
    }

    public int getGameNum(int gameId) {
        try {
            for (Map.Entry<Integer, Integer> entry : gameNumId.entrySet()) {
                if (entry.getValue() == gameId) {
                    return entry.getKey();
                }
            }
        } catch(Exception e) {
            throw new RuntimeException("invalid game number");
        }
        return gameId;
    }

    public void fillMap() throws Exception {
        GameList games = listGames();
        for(GameData game : games.games()){
            gameNumId.put(nextGameNum, game.gameID());
            nextGameNum++;
        }
    }

    public AuthTokenResponse register(UserData userData) throws Exception {
        var path = "/user";
        AuthTokenResponse authToken = this.makeRequest("POST", path, userData, AuthTokenResponse.class);
        UserContext.getInstance().setAuthToken(authToken.authToken());
        return authToken;
    }

    public AuthTokenResponse login(UserData userData) throws Exception {
        var path = "/session";
        AuthTokenResponse authToken = this.makeRequest("POST", path, userData, AuthTokenResponse.class);
        UserContext.getInstance().setAuthToken(authToken.authToken());
        return authToken;

    }

    public void logout() throws Exception {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    public GameId createGame(GameData gameData) throws Exception {
        var path = "/game";
        GameId gameId = this.makeRequest("POST", path, gameData, GameId.class);
        gameNumId.put(nextGameNum, gameId.gameID()); // put in map
        nextGameNum++;
        return gameId;
    }

    public GameList listGames() throws Exception {
        var path = "/game";
        return this.makeRequest("GET", path, null, GameList.class);
    }

    public void joinGame(JoinGameRequest joinRequest) throws Exception {
        var path = "/game";
        this.makeRequest("PUT", path, joinRequest, null);

    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        // add authToken if required
        String authToken = UserContext.getInstance().getAuthToken();
        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");

            String reqData = new Gson().toJson(request);

            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new Exception("failure");
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status >= 200 && status < 300;
    }

}
