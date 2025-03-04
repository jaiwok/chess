package server.handler;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import service.ClearService;
import service.exceptions.ErrorMsg;

public class ClearHandler implements Route {
    private final ClearService clearService;

    public ClearHandler(ClearService c) {
        clearService = c;
    }

    @Override
    public Object handle(Request req, Response res) {
        Gson json = new Gson();

        try {

            clearService.clear();
            res.type("application/json");
            res.status(200);

            return json.toJson(null);

        } catch (Exception e) {

            res.status(500);
            return json.toJson(new ErrorMsg(e.getMessage()));
        }
    }
}
