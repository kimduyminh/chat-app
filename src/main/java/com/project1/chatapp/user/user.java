package com.project1.chatapp.user;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class user {
    private String name;
    private String username;
    private String password;
    private int userId;
    private Status status;
}
