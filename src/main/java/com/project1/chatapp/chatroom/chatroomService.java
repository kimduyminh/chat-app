package com.project1.chatapp.chatroom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class chatroomService {
    private final DataSource dataSource;
    public chatroomService(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String idGenerator(){
        return UUID.randomUUID().toString();
    }
    public void createChatRoom(String name){
        String createChatRoomQuery="insert into master.dbo.chatroom (chat_id,chat_name) values (?,?)";
        String id_created = idGenerator();
        try{
            Connection connectionCreateChatRoom=dataSource.getConnection();
            PreparedStatement preparedStatementCreateChatRoom=connectionCreateChatRoom.prepareStatement(createChatRoomQuery);
            preparedStatementCreateChatRoom.setString(1,id_created);
            preparedStatementCreateChatRoom.setString(2,name);
            preparedStatementCreateChatRoom.executeUpdate();
            connectionCreateChatRoom.close();
            preparedStatementCreateChatRoom.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> listChatRoom(String user_id){
        String listUserJoinedChatQuery="select * from master.dbo.joinedchat where user_id=?";
        try{
            Connection connectionListChatRoom= dataSource.getConnection();
            PreparedStatement preparedStatementListChatRoom=connectionListChatRoom.prepareStatement(listUserJoinedChatQuery);
            preparedStatementListChatRoom.setString(1,user_id);
            ResultSet resultSetListChatRoom=preparedStatementListChatRoom.executeQuery();
            List<String> list=new ArrayList<>();
            while(resultSetListChatRoom.next()){
                list.add(resultSetListChatRoom.getString("chat_id"));
            }
            connectionListChatRoom.close();
            preparedStatementListChatRoom.close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteChatRoom(String chat_id){
        String deleteChatRoomQueryJoinedChat="delete from master.dbo.joinedchat where chat_id=?";
        String deleteChatRoomQueryChatRoom="delete from master.dbo.chatroom where chat_id=?";
        String deleteChatRoomQueryMessage="delete from master.dbo.message where chat_id=?";
        try{
            Connection connectionDeleteChatRoom= dataSource.getConnection();
            PreparedStatement preparedStatementQueryJoinedChat=connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryJoinedChat);
            preparedStatementQueryJoinedChat.setString(1,chat_id);
            preparedStatementQueryJoinedChat.executeUpdate();
            preparedStatementQueryJoinedChat.close();
            PreparedStatement preparedStatementQueryChatRoom=connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryChatRoom);
            preparedStatementQueryChatRoom.setString(1,chat_id);
            preparedStatementQueryChatRoom.executeUpdate();
            preparedStatementQueryChatRoom.close();
            PreparedStatement preparedStatementQueryMessage=connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryMessage);
            preparedStatementQueryMessage.setString(1,chat_id);
            preparedStatementQueryMessage.executeUpdate();
            preparedStatementQueryMessage.close();
            connectionDeleteChatRoom.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void inviteToChatRoom(String chat_id, String user_id){
        String inviteToChatRoomQuery="insert into master.dbo.joinedchat (chat_id,user_id) values (?,?)";
        try{
            Connection connectionInviteToChatRoom= dataSource.getConnection();
            PreparedStatement preparedStatementInviteToChatRoom=connectionInviteToChatRoom.prepareStatement(inviteToChatRoomQuery);
            preparedStatementInviteToChatRoom.setString(1,chat_id);
            preparedStatementInviteToChatRoom.setString(2,user_id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
