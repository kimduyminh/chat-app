package com.project1.chatapp.chatroom;

import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project1.chatapp.message.messageService;

import java.util.List;

@RestController
public class chatroomController {
    @Autowired
    private chatroomService chatroomService;
    @Autowired
    private messageService messageService;
    @Autowired
    private sessionService sessionService;
    @PostMapping("/app/{session_id}/createChatroom")
    public ResponseEntity<String> createChatroom(@PathVariable String session_id, @RequestBody chatroomService.newGroup newGroup) {
        String newChatId=chatroomService.createChatRoom(newGroup,session_id);
        System.out.println(newChatId+ " new chat id");
        return ResponseEntity.ok("OK");
    }
    @GetMapping("/app/{session_id}/loadchat")
    public List<chatroomService.chatroomInfo> loadChat(@PathVariable String session_id) {
        return chatroomService.listChatRoom(session_id);
    }

    @GetMapping("/app/{session_id}/{chatid}/delete")
    public String deleteChat(@PathVariable String session_id,@PathVariable String chatid) {
        chatroomService.deleteChatRoom(session_id,chatid);
        return "Chatroom deleted";
    }
    @GetMapping("/app/{session_id}/{chat_id}/{user_id}/add")
    public String addToChatRoom(@PathVariable String session_id,@PathVariable String chat_id,@PathVariable String user_id) {
        chatroomService.addToChatRoom(session_id,chat_id,user_id);
        return "User added";
    }
    @GetMapping("/app/{session_id}/{chat_id}/changename")
    public String changeName(@PathVariable String session_id, @PathVariable String chat_id, @RequestBody String name) {
        chatroomService.changeChatroomName(session_id,chat_id,name);
        return "Chatroom name changed";
    }
    @GetMapping("app/{session_id}/{chat_id}/kick")
    public String kickFromChatroom(@PathVariable String session_id,@PathVariable String chat_id,@RequestBody String user_id) {
        chatroomService.kickFromChatroom(session_id,chat_id,user_id);
        return "User kicked";
    }
    
}
