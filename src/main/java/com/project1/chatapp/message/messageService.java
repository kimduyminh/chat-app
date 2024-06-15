package com.project1.chatapp.message;

import com.project1.chatapp.chatroom.chatroomService;
import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class messageService {
    @Autowired
    private message message;
    @Autowired
    private sessionService sessionService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private chatroomService chatRoomService;
    public void newMessage(message message, String session_id, String chat_id){
        // Sử dụng đối tượng 'message' được truyền vào thay vì 'message1' được inject
        System.out.println(session_id + " calling newMessage + " + getUserIdFromSession(session_id));
        if(sessionService.checkSession(session_id)){
            String newMessageQuery = "insert into master.dbo.message (user_id, chat_id, message, time) values (?, ?, ?, ?)";
            try(Connection newMessageConnection = dataSource.getConnection();
                PreparedStatement newMessagePreparedStatement = newMessageConnection.prepareStatement(newMessageQuery)) {

                // Sử dụng getters từ đối tượng 'message' được truyền vào
                newMessagePreparedStatement.setString(1, getUserIdFromSession(session_id));
                newMessagePreparedStatement.setString(2, chat_id);
                newMessagePreparedStatement.setString(3, message.getMessage());
                newMessagePreparedStatement.setTimestamp(4, message.getTime());
                newMessagePreparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getUserIdFromSession(String session_id){
        String userId="";
        String getUserIdFromSessionQuery="select user_id from master.dbo.sessions where session_id=?";
        try {
            Connection getUserIdFromSessionConnection=dataSource.getConnection();
            PreparedStatement getUserIdFromSessionStatement=getUserIdFromSessionConnection.prepareStatement(getUserIdFromSessionQuery);
            getUserIdFromSessionStatement.setString(1,session_id);
            ResultSet getUserIdFromSessionResult=getUserIdFromSessionStatement.executeQuery();
            if (getUserIdFromSessionResult.next()){
                userId=getUserIdFromSessionResult.getString("user_id");
            }
            getUserIdFromSessionConnection.close();
            getUserIdFromSessionStatement.close();
            getUserIdFromSessionResult.close();
            return userId;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Map<String, Object> listMessages(String session_id, String chat_id) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("calling listMessages");
        if (sessionService.checkSession(session_id)) {
            if (chatRoomService.checkUserExistsInChatroom(session_id, chat_id)) {
                List<message> listMessageData = new ArrayList<>();
                String listMessageQuery = "select * from master.dbo.message where chat_id = ?";
                try (Connection listMessageConnection = dataSource.getConnection();
                     PreparedStatement listMessagePreparedStatement = listMessageConnection.prepareStatement(listMessageQuery)) {

                    listMessagePreparedStatement.setString(1, chat_id);
                    try (ResultSet listMessageResultSet = listMessagePreparedStatement.executeQuery()) {
                        while (listMessageResultSet.next()) {
                            message message = new message();
                            message.setChat_id(chat_id);
                            message.setUser_id(listMessageResultSet.getString("user_id"));
                            message.setMessage(listMessageResultSet.getString("message"));
                            message.setTime(listMessageResultSet.getTimestamp("time"));
                            message.setSentBySession(message.getUser_id().equals(sessionService.getUserIdFromSession(session_id)));
                            listMessageData.add(message);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                response.put("messages", listMessageData); // Add the messages to the response map
                return response; // Return the map instead of just the list
            } else {
                response.put("messages", new ArrayList<>()); // Return empty list in the map
                return response;
            }
        }
        response.put("messages", new ArrayList<>()); // Return empty list in the map
        return response;
    }

}
