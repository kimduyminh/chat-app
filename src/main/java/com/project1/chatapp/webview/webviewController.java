package com.project1.chatapp.webview;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
}
