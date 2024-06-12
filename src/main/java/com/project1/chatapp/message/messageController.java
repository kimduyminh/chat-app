package com.project1.chatapp.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project1.chatapp.sessions.sessionService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class messageController {
    @Autowired
    private messageService messageService;
    @Autowired
    private sessionService sessionService;

    @MessageMapping("/app/{session_id}/{chat_id}/sendm")
    @SendTo("/app/{session_id}/{chat_id}")
    public message newMessage(@Payload message message,String chat_id,String session_id) {
        System.out.println("message received");
        messageService.newMessage(message,chat_id,session_id);
        return message;
    }
    @GetMapping("/app/{session_id}/{chat_id}/loadm")
    public List<message> listMessages(@PathVariable("session_id") String session_id, @PathVariable("chat_id")String chat_id) {
        return messageService.listMessages(session_id,chat_id);
    }

}
