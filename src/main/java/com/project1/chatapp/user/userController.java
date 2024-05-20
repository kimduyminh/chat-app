package com.project1.chatapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class userController {
    @Autowired
    private userService userService;
    @PostMapping("/app.login")
    public ResponseEntity<String> login(@RequestBody userService.loginInfo loginInfo){
        return userService.login(loginInfo);
    }
    @PostMapping("/app.signup")
    public ResponseEntity<String> signup(@RequestBody userService.signupInfo signupInfo){
        return userService.signUp(signupInfo);
    }
}
