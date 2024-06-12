package com.project1.chatapp.config;


import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;

import java.util.Map;
@Component
public class webSocketInterceptor {
    @Autowired
    private sessionService session;
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String sessionId = request.getHeaders().getFirst("session-id");
        return session.checkSession(sessionId);
    }
}
