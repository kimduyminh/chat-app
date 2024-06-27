package com.project1.chatapp.message;

import com.project1.chatapp.Security.encryptionService;
import com.project1.chatapp.chatroom.chatroomService;
import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.project1.chatapp.user.userService;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class messageService {
    @Autowired
    private sessionService sessionService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private chatroomService chatRoomService;
    @Autowired
    private encryptionService encryptionService;
    @Autowired
    private userService userService;
    @Async("AsyncExecutor")
    public void newMessage(message message, String session_id, String chat_id){
        System.out.println(session_id + " calling newMessage + " + getUserIdFromSession(session_id));
        if(sessionService.checkSession(session_id)){
            String newMessageQuery = "insert into master.dbo.message (user_id, chat_id, message, time) values (?, ?, ?, ?) ";
            try(Connection newMessageConnection = dataSource.getConnection();
                PreparedStatement newMessagePreparedStatement = newMessageConnection.prepareStatement(newMessageQuery)) {

                newMessagePreparedStatement.setString(1, getUserIdFromSession(session_id).get());
                newMessagePreparedStatement.setString(2, chat_id);
                newMessagePreparedStatement.setString(3, encryptionService.encrypt(message.getMessage()));
                newMessagePreparedStatement.setTimestamp(4, message.getTime());
                newMessagePreparedStatement.executeUpdate();
            } catch (SQLException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public CompletableFuture<String> getUserIdFromSession(String session_id){
        String getUserIdFromSessionQuery="select user_id from master.dbo.sessions where session_id=?";
        try {
            String userId="";
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
            return CompletableFuture.completedFuture(userId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Async("AsyncExecutor")
    public CompletableFuture<Map<String, Object>> listMessages(String session_id, String chat_id) {
        String m="messages";
        Map<String, Object> response = new HashMap<>();
        System.out.println("calling listMessages");
        if (sessionService.checkSession(session_id)) {
            if (chatRoomService.checkUserExistingInChatroom(session_id, chat_id)) {
                List<message> listMessageData = new ArrayList<>();
                String listMessageQuery = "select * from master.dbo.message where chat_id = ? ORDER BY [time]";
                try (Connection listMessageConnection = dataSource.getConnection();
                     PreparedStatement listMessagePreparedStatement = listMessageConnection.prepareStatement(listMessageQuery)) {

                    listMessagePreparedStatement.setString(1, chat_id);
                    try (ResultSet listMessageResultSet = listMessagePreparedStatement.executeQuery()) {
                        while (listMessageResultSet.next()) {
                            message message = new message();
                            message.setChat_id(chat_id);
                            String user_id=listMessageResultSet.getString("user_id");
                            message.setUser_id(user_id);
                            message.setName(userService.getUserNameFromId(user_id));
                            message.setMessage(encryptionService.decrypt(listMessageResultSet.getString("message")));
                            message.setTime(listMessageResultSet.getTimestamp("time"));
                            message.setSentBySession(message.getUser_id().equals(sessionService.getUserIdFromSession(session_id)));
                            listMessageData.add(message);
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                response.put(m, listMessageData);
            } else {
                response.put(m, new ArrayList<>());
            }
            return CompletableFuture.completedFuture(response);
        }
        response.put(m, new ArrayList<>());
        return CompletableFuture.completedFuture(response);
    }

}
