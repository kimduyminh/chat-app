package com.project1.chatapp.chatroom;

import com.project1.chatapp.sessions.sessionService;
import com.project1.chatapp.user.userService;

import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    public static class chatroomInfo {
        private String name;
        private String chat_id;
    }
    @Component
    @Getter
    @Setter
    public static class newGroup {
        private String name;
    }
    @Component
    @Getter
    @Setter
    public static class name{
        private String name;
    }
    @Autowired
    private sessionService sessionService;
    @Autowired
    private userService userService;
    public chatroomService(@Qualifier("dataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String idGenerator(){
        return UUID.randomUUID().toString();
    }
    public void createChatRoom(newGroup newGroup,String session_id){
        if (sessionService.checkSession(session_id)){
            String createChatRoomQuery="insert into master.dbo.chatroom (chat_id,chat_name) values (?,?)";
            String addUserToChatroomQuery="insert into master.dbo.joinedchat (chat_id,user_id) values (?,?)";
            String id_created = idGenerator();
            try{
                Connection connectionCreateChatRoom=dataSource.getConnection();
                PreparedStatement preparedStatementCreateChatRoom=connectionCreateChatRoom.prepareStatement(createChatRoomQuery);
                preparedStatementCreateChatRoom.setString(1,id_created);
                preparedStatementCreateChatRoom.setString(2, newGroup.name);
                preparedStatementCreateChatRoom.executeUpdate();
                PreparedStatement preparedStatementAddUserToChatroom=connectionCreateChatRoom.prepareStatement(addUserToChatroomQuery);
                preparedStatementAddUserToChatroom.setString(1,id_created);
                preparedStatementAddUserToChatroom.setString(2,sessionService.getUserIdFromSession(session_id));
                preparedStatementAddUserToChatroom.executeUpdate();
                connectionCreateChatRoom.close();
                preparedStatementCreateChatRoom.close();
                preparedStatementAddUserToChatroom.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<chatroomInfo> listChatRoom(String session_id) {
        if (sessionService.checkSession(session_id)) {
            String user_id = sessionService.getUserIdFromSession(session_id);
            String listUserJoinedChatQuery = "SELECT * FROM master.dbo.joinedchat WHERE user_id=?";
            List<chatroomInfo> listChatroom = new ArrayList<>();
            try (
                    Connection connectionListChatRoom = dataSource.getConnection();
                    PreparedStatement preparedStatementListChatRoom = connectionListChatRoom.prepareStatement(listUserJoinedChatQuery)
            ) {
                preparedStatementListChatRoom.setString(1, user_id);
                try (ResultSet resultSetListChatRoom = preparedStatementListChatRoom.executeQuery()) {
                    while (resultSetListChatRoom.next()) {
                        String chat_id = resultSetListChatRoom.getString("chat_id");
                        chatroomInfo chatroomInfo = new chatroomInfo();
                        chatroomInfo.name = getChatroomName(chat_id);
                        chatroomInfo.chat_id = chat_id;
                        listChatroom.add(chatroomInfo);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return listChatroom;
        } else {
            return null;
        }
    }

    public void deleteChatRoom(String chat_id, String session_id) {
        System.out.println("Entered deleteChatRoom function");
        if (sessionService.checkSession(session_id)) {
            if (checkUserExistingInChatroom(session_id, chat_id)) {
                String deleteChatRoomQueryJoinedChat = "DELETE FROM master.dbo.joinedchat WHERE chat_id = ?";
                String deleteChatRoomQueryChatRoom = "DELETE FROM master.dbo.chatroom WHERE chat_id = ?";
                String deleteChatRoomQueryMessage = "DELETE FROM master.dbo.message WHERE chat_id = ?";
                try (Connection connectionDeleteChatRoom = dataSource.getConnection()) {

                    try (PreparedStatement preparedStatementQueryJoinedChat = connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryJoinedChat)) {
                        preparedStatementQueryJoinedChat.setString(1, chat_id);
                        preparedStatementQueryJoinedChat.executeUpdate();
                    }

                    try (PreparedStatement preparedStatementQueryChatRoom = connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryChatRoom)) {
                        preparedStatementQueryChatRoom.setString(1, chat_id);
                        preparedStatementQueryChatRoom.executeUpdate();
                    }

                    try (PreparedStatement preparedStatementQueryMessage = connectionDeleteChatRoom.prepareStatement(deleteChatRoomQueryMessage)) {
                        preparedStatementQueryMessage.setString(1, chat_id);
                        preparedStatementQueryMessage.executeUpdate();
                    }

                } catch (SQLException e) {
                    throw new RuntimeException("Error deleting chat room: " + e.getMessage(), e);
                }
            } else {
                System.out.println("User does not exist in chatroom");
            }
        } else {
            System.out.println("Invalid session");
        }
    }

    public void addToChatRoom(String session_id, String chat_id, String user_id) {
        if (sessionService.checkSession(session_id)) {
            if (!checkUserExistsInChatroom(session_id, user_id, chat_id)) {
                String inviteToChatRoomQuery = "INSERT INTO master.dbo.joinedchat (chat_id, user_id) VALUES (?, ?)";
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(inviteToChatRoomQuery)) {

                    System.out.println("Adding user to chatroom: " + chat_id + ", user: " + user_id);
                    preparedStatement.setString(1, chat_id);
                    preparedStatement.setString(2, user_id);
                    int rowsAffected = preparedStatement.executeUpdate();
                    System.out.println("Rows affected: " + rowsAffected);

                } catch (SQLException e) {
                    System.err.println("SQL Error: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("User already exists in chatroom: " + chat_id + ", user: " + user_id);
            }
        } else {
            System.out.println("Invalid session: " + session_id);
        }
    }

    public boolean checkUserExistsInChatroom(String session_id, String user_id, String chat_id) {
        if (sessionService.checkSession(session_id)) {
            String checkUserExistsQuery = "SELECT * FROM master.dbo.joinedchat WHERE chat_id = ? AND user_id = ?";
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(checkUserExistsQuery)) {

                preparedStatement.setString(1, chat_id);
                preparedStatement.setString(2, user_id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    boolean exists = resultSet.next();
                    System.out.println("User exists in chatroom: " + exists);
                    return exists;
                }
            } catch (SQLException e) {
                System.err.println("SQL Error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public String getChatroomName(String chat_id) {
        String getChatroomNameQuery = "SELECT chat_name FROM master.dbo.chatroom WHERE chat_id=?";
        try (Connection getChatroomNameConnection = dataSource.getConnection();
             PreparedStatement preparedStatementGetChatroomName = getChatroomNameConnection.prepareStatement(getChatroomNameQuery)) {

            preparedStatementGetChatroomName.setString(1, chat_id);
            try (ResultSet resultSetGetChatroomName = preparedStatementGetChatroomName.executeQuery()) {
                if (resultSetGetChatroomName.next()) {
                    return resultSetGetChatroomName.getString("chat_name");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUserExistingInChatroom(String session_id, String chat_id) {
        if (sessionService.checkSession(session_id)) {
            String user_id = sessionService.getUserIdFromSession(session_id);
            String checkUserExistsQuery = "SELECT * FROM master.dbo.joinedchat WHERE chat_id = ? AND user_id = ?";

            try (Connection checkUserExistsConnection = dataSource.getConnection();
                 PreparedStatement preparedStatementCheckUserExists = checkUserExistsConnection.prepareStatement(checkUserExistsQuery)) {
                preparedStatementCheckUserExists.setString(1, chat_id);
                preparedStatementCheckUserExists.setString(2, user_id);

                try (ResultSet resultSetCheckUserExists = preparedStatementCheckUserExists.executeQuery()) {
                    return resultSetCheckUserExists.next();
                }

            } catch (SQLException e) {
                throw new RuntimeException("Error checking if user exists in chatroom: " + e.getMessage(), e);
            }
        }
        return false;
    }

    public void changeChatroomName(String session_id,String chat_id,name name){
        if(sessionService.checkSession(session_id)){
            if (checkUserExistingInChatroom(session_id,chat_id)){
                String changeChatroomNameQuery="update master.dbo.chatroom set chat_name=? where chat_id=?";
                try{
                    Connection changeChatroomNameConnection=dataSource.getConnection();
                    PreparedStatement changeChatroomNameStatement=changeChatroomNameConnection.prepareStatement(changeChatroomNameQuery);
                    changeChatroomNameStatement.setString(1,name.name);
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
            if(checkUserExistsInChatroom(session_id,chat_id,kick_user_id)){
                String kickUserQuery="delete from master.dbo.joinedchat where user_id=? and chat_id=?";
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

    public List<userService.userPublic> listUsersInChatroom(String session_id,String chat_id){
        List<userService.userPublic> list =new ArrayList<>();
        if(sessionService.checkSession(session_id)){
            String listUsersInChatroomQuery="select user_id from master.dbo.joinedchat where chat_id=?";
            try{
                Connection listUsersInChatroomConnection = dataSource.getConnection();
                PreparedStatement listUsersInChatroomStatement=listUsersInChatroomConnection.prepareStatement(listUsersInChatroomQuery);
                listUsersInChatroomStatement.setString(1,chat_id);
                ResultSet resultSetListUsersInChatroom=listUsersInChatroomStatement.executeQuery();
                while (resultSetListUsersInChatroom.next()){
                    String user_id=resultSetListUsersInChatroom.getString("user_id");
                    com.project1.chatapp.user.userService.userPublic userPublic =userService.findUserInChat(user_id);
                    list.add(userPublic);
                }
                return list;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else{
            return list;
        }
    }
}
