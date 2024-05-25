package com.project1.chatapp.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.sql.Timestamp;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class message {
    private String user_id;
    private String message;
    private String chat_id;
    private Timestamp time;

    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }
}
