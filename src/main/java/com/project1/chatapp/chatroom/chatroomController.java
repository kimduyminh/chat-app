package com.project1.chatapp.chatroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project1.chatapp.message.messageService;

import java.util.List;

@RestController
public class chatroomController {
    @Autowired
    private chatroomService chatroomService;
    @Autowired
    private messageService messageService;
    @PostMapping("/app/{session_id}/createChatroom")
    public ResponseEntity<String> createChatroom(@PathVariable String session_id,String name) {
        chatroomService.createChatRoom(session_id,name);
        return ResponseEntity.ok("Chatroom created");
    }
    @GetMapping("/app/{session_id}/loadchat")
    public List<chatroomService.chatroomInfo> loadChat(@PathVariable String session_id) {
        return chatroomService.listChatRoom(session_id);
    }
    @PostMapping("/app/{session_id}/{chatid}/delete")
    
}
