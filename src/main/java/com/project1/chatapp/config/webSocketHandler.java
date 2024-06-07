package com.project1.chatapp.config;

import com.project1.chatapp.sessions.sessionService;
import com.project1.chatapp.user.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Configuration
public class webSocketHandler implements WebSocketHandler {
    @Autowired
    private sessionService sessionService;
    @Autowired
    private userService userservice;
    private final Map<WebSocketSession, String> sessionMapping = new ConcurrentHashMap<>();
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessionMapping.put(session,userservice.tempSession);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = sessionMapping.remove(session);
        sessionService.deleteSession(sessionId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
