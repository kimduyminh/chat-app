package com.project1.chatapp.message;

import com.project1.chatapp.config.timeStampConverter;
import com.project1.chatapp.sessions.sessionService;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.Map;

@RestController
public class messageController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private messageService messageService;
    @Autowired
    private sessionService sessionService;

    @Getter
    @Setter
    @Component
    public static class sessionInfo{
        private String session_id;
        private String chat_id;
        private String message;
        private String timestamp;
    }
    @Autowired
    private timeStampConverter timeStampConverter;

    @MessageMapping("/{session_id}/{chat_id}/sendm")
    @SendTo("/topic/{session_id}/{chat_id}")
    public void newMessage(@Payload sessionInfo sessionInfo, @DestinationVariable String chat_id, @DestinationVariable String session_id) {
        if (sessionService.checkSession(session_id)){
            messagingTemplate.convertAndSend("/topic/" + chat_id, sessionInfo.getMessage());

            message newMessage = new message();
            newMessage.setUser_id(sessionService.getUserIdFromSession(sessionInfo.getSession_id()));
            newMessage.setChat_id(sessionInfo.getChat_id());
            newMessage.setMessage(sessionInfo.getMessage());

            String formattedTimestamp = timeStampConverter.convertTimestamp(sessionInfo.getTimestamp());
            newMessage.setTime(Timestamp.valueOf(formattedTimestamp));

            messageService.newMessage(newMessage, sessionInfo.getSession_id(), sessionInfo.getChat_id());
        }
        else{
            System.out.println("Intruder detected");
        }
    }

    @GetMapping("/app/{session_id}/{chat_id}/loadm")
    public Map<String, Object> listMessages(@PathVariable("session_id") String session_id, @PathVariable("chat_id")String chat_id) {
        return messageService.listMessages(session_id, chat_id);
    }

}
