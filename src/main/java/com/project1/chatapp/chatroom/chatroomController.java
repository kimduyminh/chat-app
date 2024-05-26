package com.project1.chatapp.chatroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class chatroomController {
    @Autowired
    private chatroomService chatroomService;

}
