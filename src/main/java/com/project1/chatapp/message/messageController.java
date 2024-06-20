package com.project1.chatapp.message;

import lombok.Getter;
import lombok.Setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project1.chatapp.sessions.sessionService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class messageController {
    @Autowired
    private messageService messageService;
    @Autowired
    private sessionService sessionService;
    @Autowired
    private DataSource dataSource;

    @Getter
    @Setter
    @Component
    public static class sessionInfo{
        private String session_id;
        private String chat_id;
        private String message;
        private String timestamp;
    }

    @MessageMapping("/{session_id}/{chat_id}/sendm")
    @SendTo("/topic/{session_id}/{chat_id}")
    public message newMessage(@Payload sessionInfo sessionInfo) {
        System.out.println("message received");
        message message = new message();
        message.setUser_id(sessionService.getUserIdFromSession(sessionInfo.getSession_id())); // Giả sử bạn có phương thức này
        message.setChat_id(sessionInfo.getChat_id());
        message.setMessage(sessionInfo.getMessage());
        String formattedTimestamp = TimestampConverter.convertTimestamp(sessionInfo.getTimestamp()); // Huy Tran cook this
        message.setTime(Timestamp.valueOf(formattedTimestamp)); // Chuyển đổi String thành Timestamp

        System.out.println(message.getUser_id());
        System.out.println(message.getChat_id());

        // Gọi service với đối tượng 'message' mới tạo
        messageService.newMessage(message, sessionInfo.getSession_id(), sessionInfo.getChat_id());

        return message;
    }

    public class TimestampConverter {
        private static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        private static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        public static String convertTimestamp(String inputTimestamp) {
            try {
                Date parsedDate = inputFormat.parse(inputTimestamp);
                return outputFormat.format(parsedDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @GetMapping("/app/{session_id}/{chat_id}/loadm")
    public Map<String, Object> listMessages(@PathVariable("session_id") String session_id, @PathVariable("chat_id")String chat_id) {
        return messageService.listMessages(session_id, chat_id);
    }

}
