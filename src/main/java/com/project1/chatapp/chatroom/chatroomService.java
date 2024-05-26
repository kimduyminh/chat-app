package com.project1.chatapp.chatroom;

import com.project1.chatapp.sessions.sessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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
    @Component
    public static class chatroomInfo {
        private String name;
        private String chat_id;
    }
    @Autowired
    private sessionService sessionService;
    public chatroomService(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String idGenerator(){
        return UUID.randomUUID().toString();
    }
    public void createChatRoom(String name,String session_id){
        if (sessionService.checkSession(session_id)){
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
        }}
    }
    public List<chatroomInfo> listChatRoom(String session_id){
        if (sessionService.checkSession(session_id)){
            String user_id=sessionService.getUserIdFromSession(session_id);
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
                List<chatroomInfo> listChatroom=new ArrayList<>();
                for (String i:list){
                    chatroomInfo chatroomInfo=new chatroomInfo();
                    chatroomInfo.name = getChatroomName(i);
                    chatroomInfo.chat_id=i;
                    listChatroom.add(chatroomInfo);
                }
                return listChatroom;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            return null;
        }
    }
    public void deleteChatRoom(String chat_id,String session_id){
        if (sessionService.checkSession(session_id)){
            if(checkUserExistsInChatroom(session_id,chat_id)){
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
        }
    }
    public void addToChatRoom(String chat_id, String user_id,String session_id){
        if (sessionService.checkSession(session_id)){
            if(checkUserExistsInChatroom(session_id,chat_id)){
                String inviteToChatRoomQuery="insert into master.dbo.joinedchat (chat_id,user_id) values (?,?)";
                try{
                    Connection connectionInviteToChatRoom= dataSource.getConnection();
                    PreparedStatement preparedStatementInviteToChatRoom=connectionInviteToChatRoom.prepareStatement(inviteToChatRoomQuery);
                    preparedStatementInviteToChatRoom.setString(1,chat_id);
                    preparedStatementInviteToChatRoom.setString(2,user_id);
                    preparedStatementInviteToChatRoom.executeUpdate();
                    preparedStatementInviteToChatRoom.close();
                    connectionInviteToChatRoom.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public String getChatroomName(String chat_id){
        String getChatroomNameQuery="select * from master.dbo.chatroom where chat_id=?";
        try {
            Connection getChatroomNameConnection= dataSource.getConnection();
            PreparedStatement preparedStatementGetChatroomName=getChatroomNameConnection.prepareStatement(getChatroomNameQuery);
            preparedStatementGetChatroomName.setString(1,chat_id);
            ResultSet resultSetGetChatroomName=preparedStatementGetChatroomName.executeQuery();
            if(resultSetGetChatroomName.next()){
                getChatroomNameConnection.close();
                preparedStatementGetChatroomName.close();
                String chatroomName=resultSetGetChatroomName.getString("chat_id");
                resultSetGetChatroomName.close();
                return chatroomName;
            }else{
                getChatroomNameConnection.close();
                preparedStatementGetChatroomName.close();
                resultSetGetChatroomName.close();
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean checkUserExistsInChatroom(String session_id,String chat_id){
        if (sessionService.checkSession(session_id)){
            String user_id=sessionService.getUserIdFromSession(session_id);
            String checkUserExistsQuery="select * from master.dbo.joinedchat where chat_id=? and user_id = ?";
            try{
                Connection checkUserExistsConnection= dataSource.getConnection();
                PreparedStatement preparedStatementCheckUserExists=checkUserExistsConnection.prepareStatement(checkUserExistsQuery);
                preparedStatementCheckUserExists.setString(1,chat_id);
                preparedStatementCheckUserExists.setString(2,user_id);
                ResultSet resultSetCheckUserExists=preparedStatementCheckUserExists.executeQuery();
                boolean result=resultSetCheckUserExists.next();
                checkUserExistsConnection.close();
                preparedStatementCheckUserExists.close();
                resultSetCheckUserExists.close();
                return result;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    public void changeChatroomName(String session_id,String chat_id,String name){
        if(sessionService.checkSession(session_id)){
            if (checkUserExistsInChatroom(session_id,chat_id)){
                String changeChatroomNameQuery="update chatroom set chat_name=? where chat_id=?";
                try{
                    Connection changeChatroomNameConnection=dataSource.getConnection();
                    PreparedStatement changeChatroomNameStatement=changeChatroomNameConnection.prepareStatement(changeChatroomNameQuery);
                    changeChatroomNameStatement.setString(1,name);
                    changeChatroomNameStatement.setString(2,chat_id);
                    changeChatroomNameStatement.execute();
                    changeChatroomNameConnection.close();
                    changeChatroomNameStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public void kickFromChatroom(String session_id,String chat_id,String kick_user_id){
        if(sessionService.checkSession(session_id)){
            if(checkUserExistsInChatroom(session_id,chat_id)){
                String kickUserQuery="delete from joinedchat where user_id=? and chat_id=?";
                try{
                    Connection kickUserConnection= dataSource.getConnection();
                    PreparedStatement kickUserStatement=kickUserConnection.prepareStatement(kickUserQuery);
                    kickUserStatement.setString(1,kick_user_id);
                    kickUserStatement.setString(2,chat_id);
                    kickUserStatement.execute();
                    kickUserConnection.close();
                    kickUserStatement.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
