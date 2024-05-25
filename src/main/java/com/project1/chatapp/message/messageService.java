package com.project1.chatapp.message;

import com.project1.chatapp.chatroom.chatroomService;
import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    public void newMessage(@Payload message message,String session_id,String chat_id){
        if(sessionService.checkSession(session_id)){
            String newMessageQuery="insert into message(user_id,chat_id,message,time) values (?,?,?,?)";
            try{
                Connection newMessageConnection= dataSource.getConnection();
                PreparedStatement newMessagePreparedStatement=newMessageConnection.prepareStatement(newMessageQuery);
                newMessagePreparedStatement.setString(1,sessionService.getUserIdFromSession(session_id));
                newMessagePreparedStatement.setString(2,chat_id);
                newMessagePreparedStatement.setString(3,message.getMessage());
                newMessagePreparedStatement.setTimestamp(4,message.getTime());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public List<message> listMessages(String session_id,String chat_id){
        if(sessionService.checkSession(session_id)){
            if(chatRoomService.checkUserExistsInChatroom(session_id,chat_id)){
                List<message> listMessageData=new ArrayList<>();
                String listMessageQuery="select * from message where chat_id=?";
                try{
                    Connection listMessageConnection= dataSource.getConnection();
                    PreparedStatement listMessagePreparedStatement=listMessageConnection.prepareStatement(listMessageQuery);
                    listMessagePreparedStatement.setString(1,chat_id);
                    ResultSet listMessageResultSet=listMessagePreparedStatement.executeQuery();
                    if(listMessageResultSet.next()){
                        message message=new message();
                        message.setChat_id(chat_id);
                        message.setUser_id(listMessageResultSet.getString("user_id"));
                        message.setMessage(listMessageResultSet.getString("message"));
                        message.setTime(listMessageResultSet.getTimestamp("time"));
                        listMessageData.add(message);
                    }
                    return listMessageData;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }else{return null;}
        }
        return null;
    }
}
