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
        return userService.login(loginInfo).join();
    }
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody userService.signupInfo signupInfo){
        return userService.signUp(signupInfo).join();
    }
    @PostMapping("/app/{session_id}/find")
    public List<userService.userPublic> findUser(@PathVariable("session_id") String session_id, @RequestBody Map<String, String> payload) {
        String info = payload.get("info");
        System.out.println(info);
        return userService.findUser(session_id, info);
    }
    @GetMapping("/app/friend/{session_id}/listfriend")
    public List<com.project1.chatapp.user.userService.friend> getFriends(@PathVariable("session_id") String session_id){
        return userService.getListFriend(session_id);
    }
    @GetMapping("/app/friend/{session_id}/loadRequestReceived")
    public List<userService.friendRequestReceived> getRequestsReceived(@PathVariable("session_id") String session_id){
        return userService.loadFriendRequestReceived(session_id);
    }
    @PostMapping("/app/friend/{session_id}/{user_id}/sendRequest")
    public ResponseEntity<String> sendFriendRequest(@PathVariable String session_id, @PathVariable String user_id){
        userService.sendFriendRequest(session_id,user_id);
        return ResponseEntity.ok("OK");
    }
    @PostMapping("/app/friend/{session_id}/{user_id}/accept")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable String session_id, @PathVariable String user_id){
        userService.acceptFriendRequest(session_id,user_id);
        return ResponseEntity.ok("OK");
    }
    @PostMapping("/app/friend/{session_id}/{user_id}/refuse")
    public ResponseEntity<String> refuseFriendRequest(@PathVariable String session_id, @PathVariable String user_id){
        userService.refuseFriendRequest(session_id,user_id);
        return ResponseEntity.ok("OK");
    }
    @GetMapping("/app/friend/{session_id}/loadRequestSent")
    public List<userService.friendRequestSent> getRequestsSent(@PathVariable("session_id") String session_id){
        return userService.loadFriendRequestSent(session_id);
    }
}
