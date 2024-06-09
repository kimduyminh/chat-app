package com.project1.chatapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;

@RestController
public class userController {
    @Autowired
    private userService userService;
    @PostMapping("/app.login")
    public ResponseEntity<String> login(@RequestBody userService.loginInfo loginInfo){
        return userService.login(loginInfo);
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody userService.signupInfo signupInfo){
        return userService.signUp(signupInfo);
    }
    @GetMapping("/friend/{session_id}")
    public List<userService.friend> getFriends(@PathVariable("session_id") String session_id){
        return userService.getListFriend(session_id);
    }
    @GetMapping("/app/{session_id}/find")
    @PostMapping("/app/{session_id}/find")
    public List<userService.userPublic> findUser(@PathVariable("session_id") String session_id, @RequestBody Map<String, String> payload) {
        String info = payload.get("info");
        return userService.findUser(session_id, info);
    }


}
