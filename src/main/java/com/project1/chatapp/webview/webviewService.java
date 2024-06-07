package com.project1.chatapp.webview;

import org.springframework.stereotype.Service;

@Service
public class webviewService {
    public String loadLoginPage(){
        return "forward:/login.html";
    }
}
