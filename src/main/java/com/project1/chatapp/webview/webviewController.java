package com.project1.chatapp.webview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class webviewController {
    @Autowired
    private webviewService webviewService;
    @GetMapping("/")
    public String loadLogin(){
        return "redirect:/login";
    }
    @GetMapping("/login")
    public String login(){
        return webviewService.loadLoginPage();
    }
    @GetMapping("/mainchat/{session_id}")
    public String forwardMainchat(@PathVariable String session_id){
        return "redirect:/mainchat.html?id="+session_id;
    }
}
