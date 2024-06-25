CREATE TABLE [user] (
                        user_id NVARCHAR(50) PRIMARY KEY,
                        username NVARCHAR(50) NOT NULL,
                        password NVARCHAR(100) NOT NULL,
                        name NVARCHAR(100) NOT NULL
);

CREATE TABLE friendship (
                            user_id1 NVARCHAR(50),
                            user_id2 NVARCHAR(50),
                            status_id INT,
                            PRIMARY KEY(user_id1, user_id2),
                            FOREIGN KEY(user_id1) REFERENCES [user](user_id),
                            FOREIGN KEY(user_id2) REFERENCES [user](user_id)
);

CREATE TABLE message (
                         user_id NVARCHAR(50) REFERENCES [user](user_id),
                         chat_id NVARCHAR(50),
                         message NVARCHAR(MAX),
                         [time] DATETIME DEFAULT GETDATE()
);

CREATE TABLE joinedchat (
                            user_id NVARCHAR(50) REFERENCES [user](user_id),
                            chat_id NVARCHAR(50)
);

CREATE TABLE chatroom (
                          chat_id NVARCHAR(50) PRIMARY KEY,
                          chat_name NVARCHAR(50)
);

CREATE TABLE sessions (
                          user_id NVARCHAR(50),
                          session_id NVARCHAR(50) PRIMARY KEY
);

GO

CREATE TRIGGER prevent_duplicate_friendships
    ON friendship
    INSTEAD OF INSERT
    AS
BEGIN
    IF EXISTS (
        SELECT 1
        FROM friendship
                 JOIN inserted ON (friendship.user_id1 = inserted.user_id2 AND friendship.user_id2 = inserted.user_id1)
            AND friendship.status_id = 1
    )
        BEGIN
            RAISERROR ('Duplicate friendship detected with reversed user IDs', 16, 1);
            ROLLBACK TRANSACTION;
            RETURN;
        END;

    INSERT INTO friendship (user_id1, user_id2, status_id)
    SELECT user_id1, user_id2, status_id
    FROM inserted;
END;
