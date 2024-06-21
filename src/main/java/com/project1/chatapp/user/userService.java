package com.project1.chatapp.user;

import com.project1.chatapp.sessions.sessionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service

public class userService {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private sessionService sessionService;

    @Getter
    @Setter
    @Component
    public static class loginInfo {
        private String username;
        private String password;
    }
    @Component
    public static class friend {
        private String username;
        private String user_id;
    }
    @Getter
    @Setter
    @Component
    public static class signupInfo {
        private String name;
        private String username;
        private String password;
    }
    @Component
    @Getter
    @Setter
    public static class userPublic{
        private String name;
        private String user_id;
    }
    @Component
    public static class friendRequest {
       private String user_id1;
       private String user_id2;
       private int status_id;
    }
    public String tempSession;
    public ResponseEntity<String> login(@RequestBody loginInfo loginInfo) {
        System.out.println("Connected successfully");
        String loginQuery = "SELECT * FROM master.dbo.[user] where username =? and password=?";
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement(loginQuery);
            ps.setString(1, loginInfo.username);
            ps.setString(2, loginInfo.password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            System.out.println("breakpoint");
            if (rs.getString("username") != null && rs.getString("password") != null) {
                String user_id = rs.getString("user_id");
                String session_id=sessionService.newSession(user_id);
                tempSession=session_id;
                System.out.println(user_id);
                ps.close();
                conn.close();
                rs.close();
                return ResponseEntity.status(HttpStatus.OK).body(session_id);
            } else {
                rs.close();
                ps.close();
                conn.close();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed, please recheck your credentials");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String idGenerator() {
        return UUID.randomUUID().toString();
    }

    public ResponseEntity<String> signUp(@RequestBody signupInfo signupInfo) {
        String checkExistenceQuery = "SELECT * FROM master.dbo.[user] WHERE username = ? AND password = ?";
        String signupQuery = "INSERT INTO master.dbo.[user] (user_id, username, password, name) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(checkExistenceQuery)) {

            preparedStatement.setString(1, signupInfo.getUsername());
            preparedStatement.setString(2, signupInfo.getPassword());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) { // User does not exist
                String id_created = idGenerator();
                try (Connection connection1 = dataSource.getConnection();
                     PreparedStatement preparedStatement1 = connection1.prepareStatement(signupQuery)) {

                    preparedStatement1.setString(1, id_created);
                    preparedStatement1.setString(2, signupInfo.username);
                    preparedStatement1.setString(3, signupInfo.password);
                    preparedStatement1.setString(4, signupInfo.name);
                    preparedStatement1.executeUpdate();

                    String session_id = sessionService.newSession(id_created);
                    return ResponseEntity.status(HttpStatus.OK).body("{\"session_id\":\"" + session_id + "\"}");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\":\"User already exists, please login\"}");
            }
        } catch (SQLException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"" + ex.getMessage() + "\"}");
        }
    }

    private String getUserNameFromId (String user_id) {
        String getName="select name from [user] where user_id=?";
        try {
            Connection getNameConnection= dataSource.getConnection();
            PreparedStatement getNameStatement=getNameConnection.prepareStatement(getName);
            getNameStatement.setString(1,user_id);
            ResultSet getNameResult = getNameStatement.executeQuery();
            if (getNameResult.next()) {
                String name=getNameResult.getString("name");
                getNameStatement.close();
                getNameConnection.close();
                getNameResult.close();
                return name;
            }else{
                getNameStatement.close();
                getNameConnection.close();
                getNameResult.close();
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUserNameFromSession(String session_id) {
        String user_id = sessionService.getUserIdFromSession(session_id);
        String getNameQuery = "Select name from master.dbo.[user] where user_id =?";
        try {
            String name = "";
            Connection getNameConnection = dataSource.getConnection();
            PreparedStatement getNameQ = getNameConnection.prepareStatement(getNameQuery);
            getNameQ.setString(1, user_id);
            ResultSet getNameResult = getNameQ.executeQuery();
            if (getNameResult.next()) {
                name = getNameResult.getString("name");
            }
            getNameResult.close();
            getNameQ.close();
            getNameConnection.close();
            return name;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public userPublic findUserInChat(String info){
        userPublic userPublicInChat=new userPublic();
        String findUserQuery="select user_id,name from master.dbo.[user] where user_id=? or name=?";
        try{
            Connection findUserConnection= dataSource.getConnection();
            PreparedStatement findUserStatement=findUserConnection.prepareStatement(findUserQuery);
            findUserStatement.setString(1,info);
            findUserStatement.setString(2,info);
            ResultSet findUserResultSet=findUserStatement.executeQuery();
            if (findUserResultSet.next()){
                userPublicInChat.name=findUserResultSet.getString("name");
                userPublicInChat.user_id=findUserResultSet.getString("user_id");
            }
            findUserResultSet.close();
            findUserStatement.close();
            findUserConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userPublicInChat;
    }

    public List<userPublic> findUser(String session_id, String info) {
        if (sessionService.checkSession(session_id)) {
            List<userPublic> findUserResult = new ArrayList<>();
            String findUserQuery = "SELECT user_id, name FROM master.dbo.[user] WHERE user_id LIKE ? OR name LIKE ?";

            try (Connection findUserConnection = dataSource.getConnection();
                 PreparedStatement findUserStatement = findUserConnection.prepareStatement(findUserQuery)) {

                String wildcardInfo = "%" + info + "%";
                findUserStatement.setString(1, wildcardInfo);
                findUserStatement.setString(2, wildcardInfo);
                try (ResultSet findUserResultSet = findUserStatement.executeQuery()) {
                    while (findUserResultSet.next()) {
                        userPublic userPublic = new userPublic();
                        userPublic.setName(findUserResultSet.getString("name"));
                        userPublic.setUser_id(findUserResultSet.getString("user_id"));
                        findUserResult.add(userPublic);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return findUserResult.isEmpty() ? null : findUserResult;
        }
        return null;
    }

    /*
    public List<user> getListFriendOnline(){

    }*/
    //Friendship Service
    public List<friend> getListFriend(String session_id) {
        if(sessionService.checkSession(session_id)){
            String user_id = sessionService.getUserIdFromSession(session_id);
            String getListFriendQuery = "SELECT * FROM master.dbo.friendship where user_id1 =? and status_id=1 or user_id2 =? and status_id=1";
            List<String> friend_id = new ArrayList<>();
            List<friend> friends = new ArrayList<>();
            try {
                Connection getListFriendConnection = dataSource.getConnection();
                PreparedStatement getListFriendQ = getListFriendConnection.prepareStatement(getListFriendQuery);
                getListFriendQ.setString(1, user_id);
                getListFriendQ.setString(2, user_id);
                ResultSet getListFriendResult = getListFriendQ.executeQuery();
                if (getListFriendResult.next()) {
                    if (getListFriendResult.getString("user_id1").equals(user_id)) {
                        friend_id.add(getListFriendResult.getString("user_id2"));
                    }
                    if (getListFriendResult.getString("user_id2").equals(user_id)) {
                        friend_id.add(getListFriendResult.getString("user_id1"));
                    }
                }
                getListFriendResult.close();
                getListFriendQ.close();
                getListFriendConnection.close();
                for (String i : friend_id) {
                    friend friend = new friend();
                    friend.username=getUserNameFromId(i);
                    friend.user_id = i;
                    friends.add(friend);
                }
                return friends;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {return null;
        }
    }
    public void sendFriendRequest(String session_id, userPublic userPublic) {
        if(sessionService.checkSession(session_id)){
            String user_id=sessionService.getUserIdFromSession(session_id);
            String deletePreviousRequestQuery = "DELETE FROM master.dbo.friendship WHERE ((user_id1 = ? AND user_id2 = ?) OR (user_id1 = ? AND user_id2 = ?)) AND status_id IN (0, 2)";
            String sendFriendRequestQuery="insert into friendship(user_id1,user_id2,status_id) values(?,?,0)";
            try{
                Connection deletePreviousRequestConnection=dataSource.getConnection();
                PreparedStatement deletePreviousRequestStatement=deletePreviousRequestConnection.prepareStatement(deletePreviousRequestQuery);
                deletePreviousRequestStatement.setString(1,user_id);
                deletePreviousRequestStatement.setString(2,userPublic.user_id);
                deletePreviousRequestStatement.setString(3,userPublic.user_id);
                deletePreviousRequestStatement.setString(4,user_id);
                deletePreviousRequestStatement.close();
                deletePreviousRequestConnection.close();

                Connection sendFriendRequestConnection= dataSource.getConnection();
                PreparedStatement sendFriendRequestStatement=sendFriendRequestConnection.prepareStatement(sendFriendRequestQuery);
                sendFriendRequestStatement.setString(1,user_id);
                sendFriendRequestStatement.setString(2,userPublic.user_id);
                sendFriendRequestStatement.close();
                sendFriendRequestConnection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public List<friendRequest> loadFriendRequestSent(String session_id){
        if(sessionService.checkSession(session_id)){
            String user_id=sessionService.getUserIdFromSession(session_id);
            List<friendRequest> friendRequestSentList = new ArrayList<>();
            String loadFriendRequestSentQuery="select * from master.dbo.friendship where user_id1 = ?AND status_id IN (0, 2)";
            try{
                Connection loadFriendRequestSentConnection= dataSource.getConnection();
                PreparedStatement loadFriendRequestSentStatement=loadFriendRequestSentConnection.prepareStatement(loadFriendRequestSentQuery);
                loadFriendRequestSentStatement.setString(1,user_id);
                ResultSet loadFriendRequestSentResultSet=loadFriendRequestSentStatement.executeQuery();
                if(loadFriendRequestSentResultSet.next()){
                    friendRequest friendRequest=new friendRequest();
                    friendRequest.user_id1=user_id;
                    friendRequest.user_id2=loadFriendRequestSentResultSet.getString("user_id2");
                    friendRequest.status_id=loadFriendRequestSentResultSet.getInt("status_id");
                    friendRequestSentList.add(friendRequest);
                }
                loadFriendRequestSentResultSet.close();
                loadFriendRequestSentConnection.close();
                loadFriendRequestSentStatement.close();
                return  friendRequestSentList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            return null;
        }
    }
    public List<friendRequest> loadFriendRequestReceived(String session_id){
        if(sessionService.checkSession(session_id)){
            String user_id=sessionService.getUserIdFromSession(session_id);
            List<friendRequest> friendRequestReceivedList = new ArrayList<>();
            String loadFriendRequestReceivedQuery="select * from master.dbo.friendship where user_id2 = ?AND status_id IN (0, 2)";
            try{
                Connection loadFriendRequestReceivedConnection= dataSource.getConnection();
                PreparedStatement loadFriendRequestReceivedStatement=loadFriendRequestReceivedConnection.prepareStatement(loadFriendRequestReceivedQuery);
                loadFriendRequestReceivedStatement.setString(1,user_id);
                ResultSet loadFriendRequestReceivedResultSet=loadFriendRequestReceivedStatement.executeQuery();
                if(loadFriendRequestReceivedResultSet.next()){
                    friendRequest friendRequest=new friendRequest();
                    friendRequest.user_id1=user_id;
                    friendRequest.user_id2=loadFriendRequestReceivedResultSet.getString("user_id2");
                    friendRequest.status_id=loadFriendRequestReceivedResultSet.getInt("status_id");
                    friendRequestReceivedList.add(friendRequest);
                }
                loadFriendRequestReceivedResultSet.close();
                loadFriendRequestReceivedConnection.close();
                loadFriendRequestReceivedStatement.close();
                return  friendRequestReceivedList;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }else {
            return null;
        }
    }
}

