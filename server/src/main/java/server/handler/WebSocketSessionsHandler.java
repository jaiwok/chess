package server.handler;
import model.returnobjects.GameId;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionsHandler {

    public final ConcurrentHashMap<Integer, Set<Session>> sessionMap = new ConcurrentHashMap<>();

    public void addClientToSessionSet(int gameId, Session session) {
        sessionMap.computeIfAbsent(gameId, key -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeClientFromSessionSet(int gameId, Session session) {
        Set<Session> sessions = sessionMap.get(gameId);

        if (sessions != null) {
            sessions.remove(session);

            if (sessions.isEmpty()) {
                sessionMap.remove(gameId, sessions);
            }
        }
    }

    public Set<Session> getSessionsSet(GameId gameId) {
        return sessionMap.get(gameId);
    }

    // send a message
    public void sendMessage(String message, Session session) throws IOException {
        if(session.isOpen()){
            session.getRemote().sendString(message);
//            System.out.println("Message sent" + message);
        }
    }

    public void broadcastMessage(int gameId, String message, Session originSession) throws IOException {
        for(Session session : sessionMap.get(gameId) ){
            if(session != originSession){
                sendMessage(message, session);
            }
        }
    }

}
