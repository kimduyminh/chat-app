package com.project1.chatapp.user;

import com.project1.chatapp.sessions.sessionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    public ResponseEntity<String> login(@RequestBody loginInfo loginInfo) {
        System.out.println("Connected successfully");
        String loginQuery = "SELECT * FROM master.dbo.[user] where username =? and password=?";
        try {
            System.out.println("1");
            Connection conn = dataSource.getConnection();
            System.out.println("2");
            PreparedStatement ps = conn.prepareStatement(loginQuery);
            System.out.println("3");
            ps.setString(1, loginInfo.username);
            ps.setString(2, loginInfo.password);
            System.out.println("4");
            ResultSet rs = ps.executeQuery();
            System.out.println("5");
            rs.next();
            System.out.println("breakpoint");
            if (rs.getString("username") != null && rs.getString("password") != null) {
                System.out.println("6");
                String user_id = rs.getString("user_id");
                String session_id=sessionService.newSession(user_id);
                System.out.println("7");
                System.out.println(user_id);
                ps.close();
                System.out.println("8");
                conn.close();
                System.out.println("9");
                rs.close();
                System.out.println("10");
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
        int statusSign = 0;
        String checkExistenceQuery = "SELECT * FROM master.dbo.[user] where username =? and password=?";
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(checkExistenceQuery);
            preparedStatement.setString(1, signupInfo.username);
            preparedStatement.setString(2, signupInfo.password);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (!resultSet.next()) {
                statusSign = 1;
            }
            if (statusSign == 1) {
                String signupQuery = "insert into master.dbo.[user] (user_id, username, password, name) values (?,?,?,?)";
                System.out.println("1");
                System.out.println(signupInfo.name);
                String id_created = idGenerator();
                Connection connection1 = dataSource.getConnection();
                PreparedStatement preparedStatement1 = connection1.prepareStatement(signupQuery);
                preparedStatement1.setString(1, id_created);
                preparedStatement1.setString(2, signupInfo.username);
                preparedStatement1.setString(3, signupInfo.password);
                preparedStatement1.setString(4, signupInfo.name);
                String session_id=sessionService.newSession(id_created);
                preparedStatement1.executeUpdate();
                connection1.close();
                preparedStatement1.close();
                return ResponseEntity.status(HttpStatus.OK).body(session_id);
            } else {
                System.out.println("2");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(checkExistenceQuery);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
    public List<friend> getListFriend(String session_id) {
        if(sessionService.checkSession(session_id)){
            String user_id = sessionService.getUserIdFromSession(session_id);
            String getListFriendQuery = "SELECT * FROM master.dbo.friendship where user_id1 =? and status=1 or user_id2 =? and status=1";
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
    /*
    public List<user> getListFriendOnline(){

    }*/
}

