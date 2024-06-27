package com.project1.chatapp.config.webSocketConfig;

import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Configuration
public class webSocketHandler implements org.springframework.web.socket.WebSocketHandler {

    @Autowired
    private sessionService sessionService;

    private final Map<String, WebSocketSession> sessionMapping = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getHandshakeHeaders().getFirst("session-id");
        if (sessionService.checkSession(sessionId)) {
            sessionMapping.put(sessionId, session);
            System.out.println("WS Connected");
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid session"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = sessionMapping.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(session))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (sessionId != null) {
            sessionService.deleteSession(sessionId);
            sessionMapping.remove(sessionId);
            System.out.println("Disconnected session: " + sessionId);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // Handle incoming WebSocket messages here
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // Handle transport errors
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
