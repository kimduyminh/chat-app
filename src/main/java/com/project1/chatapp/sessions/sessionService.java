package com.project1.chatapp.sessions;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Service
public class sessionService {
    @Getter
    @Autowired
    private session session;
    @Autowired
    private DataSource dataSource;
    private String idGenerator() {
        return UUID.randomUUID().toString();
    }
    public String newSession(String user_id){
        String session_id=idGenerator();
        String addSessionQuery="insert into master.dbo.sessions(user_id,session_id) values(?,?)";
        try{
            Connection addSessionConnection=dataSource.getConnection();
            PreparedStatement addSessionPreparedStatement=addSessionConnection.prepareStatement(addSessionQuery);
            addSessionPreparedStatement.setString(1,user_id);
            addSessionPreparedStatement.setString(2,session_id);
            addSessionPreparedStatement.executeUpdate();
            addSessionPreparedStatement.close();
            addSessionConnection.close();
            return session_id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean checkSession(String session_id){
        String checkSessionQuery="select * from master.dbo.sessions where session_id=?";
        try {
            Connection checkSessionConnection=dataSource.getConnection();
            PreparedStatement checkSessionStatement=checkSessionConnection.prepareStatement(checkSessionQuery);
            checkSessionStatement.setString(1,session_id);
            ResultSet checkSessionResult=checkSessionStatement.executeQuery();
            if (checkSessionResult.next()){
                checkSessionConnection.close();
                checkSessionStatement.close();
                checkSessionResult.close();
                return true;
            }else{
                checkSessionConnection.close();
                checkSessionStatement.close();
                checkSessionResult.close();
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }/*
    public String getSessionIdFromUser(String user_id){
        String session_id="";
        String getUserIdFromSessionQuery="select session_id from master.dbo.sessions where user_id=?";
        try {
            Connection getSessionIdFromUserConnection=dataSource.getConnection();
            PreparedStatement getSessionIdFromUserStatement=getSessionIdFromUserConnection.prepareStatement(getUserIdFromSessionQuery);
            getSessionIdFromUserStatement.setString(1,user_id);
            ResultSet getSessionIdFromUserResult=getSessionIdFromUserStatement.executeQuery();
            if (getSessionIdFromUserResult.next()){
                getSessionIdFromUserResult.getString("session_id");
            }
            getSessionIdFromUserConnection.close();
            getSessionIdFromUserStatement.close();
            getSessionIdFromUserResult.close();
            return session_id;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/
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
    public void deleteSession(String session_id){
        String deleteSessionQuery="delete from master.dbo.sessions where session_id=?";
        try{
            Connection deleteSessionConnection= dataSource.getConnection();
            PreparedStatement deleteSessionStatement=deleteSessionConnection.prepareStatement(deleteSessionQuery);
            deleteSessionStatement.setString(1,session_id);
            deleteSessionStatement.execute();
            deleteSessionConnection.close();
            deleteSessionStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
