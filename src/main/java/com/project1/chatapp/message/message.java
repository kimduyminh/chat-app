package com.project1.chatapp.message;

import lombok.*;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class message {
    private String name;
    private String user_id;
    private String message;
    private String chat_id;
    private Timestamp time;
    private boolean sentBySession;
}
