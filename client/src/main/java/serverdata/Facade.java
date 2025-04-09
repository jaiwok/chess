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
import passoff.model.TestResult;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class Facade{

    private String serverUrl;
    public static Map<Integer, Integer> gameIdMap = new HashMap<>();
    public static int nextGameInt = 1;

    public Facade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public int getGameId(int gameNum){
        try {
            return gameIdMap.get(gameNum);
        } catch(Exception e) {
            throw new RuntimeException(SET_TEXT_COLOR_RED + "Invalid game number\n"  + RESET_TEXT_COLOR);
        }
    }

    public int getGameNum(int id) {
        try {
            for (Map.Entry<Integer, Integer> entry : gameIdMap.entrySet()) {
                if (entry.getValue() == id) {
                    return entry.getKey();
                }
            }
        } catch(Exception e) {
            throw new RuntimeException(SET_TEXT_COLOR_RED + "Invalid game number\n"  + RESET_TEXT_COLOR);
        }
        return id;
    }

    public void generateGameListMap() throws Exception {
        GameList games = listGames();
        for(GameData game : games.games()){
            gameIdMap.put(nextGameInt++, game.gameID());
        }
    }

    public AuthTokenResponse register(UserData userData) throws Exception {
        AuthTokenResponse authToken = this.makeRequest("/user","POST", userData, AuthTokenResponse.class);
        UserContext.getInstance().setAuthToken(authToken.authToken());
        return authToken;
    }

    public AuthTokenResponse login(UserData userData) throws Exception {
        AuthTokenResponse authToken = this.makeRequest( "/session", "POST", userData, AuthTokenResponse.class);
        UserContext.getInstance().setAuthToken(authToken.authToken());
        return authToken;
    }

    public void logout() throws Exception {
        this.makeRequest("/session", "DELETE", null, null);
    }

    public GameId createGame(GameData gameData) throws Exception {
        GameId gameId = this.makeRequest("/game", "POST", gameData, GameId.class);
        gameIdMap.put(nextGameInt++, gameId.gameID());
        return gameId; //DO I need this
    }

    public String clearDB() throws Exception{
        try {
            this.makeRequest("/db","DELETE", (Object) null, TestResult.class);
        }catch (Throwable e) {
            return e.toString();
        }
        return "Database Cleared\n";
    }

    public GameList listGames() throws Exception {
        return this.makeRequest( "/game","GET", null, GameList.class);
    }

    public void joinGame(JoinGameRequest joinRequest) throws Exception {
            this.makeRequest("/game", "PUT", joinRequest, null);
    }

    private <T> T makeRequest(String path, String method, Object request, Class<T> responseClass) throws Exception {
        try {

            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwOnFail(http);
            return readBody(http, responseClass);

        } catch (Exception e) {

            throw new Exception(e.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        String authToken = UserContext.getInstance().getAuthToken();

        if (authToken != null) {
            http.addRequestProperty("Authorization", authToken);
        }
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");

            String data = new Gson().toJson(request);

            try (OutputStream req = http.getOutputStream()) {
                req.write(data.getBytes());
            }
        }
    }

    private void throwOnFail(HttpURLConnection http) throws Exception {
        var status = http.getResponseCode();
        if (status == 403){
            throw new Exception("403");
        } else if (status == 888) {
            throw new Exception("888");
        } else if (!success(status)) {
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

    private boolean success(int status) {
        return status >= 200 && status < 300;
    }

}
