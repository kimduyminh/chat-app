create table [user] (
                        user_id NVARCHAR(50) PRIMARY KEY,
                        username NVARCHAR(50) NOT NULL,
                        password NVARCHAR(50) NOT NULL,
                        name NVARCHAR(100) NOT NULL,
                        status int
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
                         message_id INT PRIMARY KEY IDENTITY(1,1),
                         user_id NVARCHAR(50) FOREIGN KEY REFERENCES [user],
                         chat_id NVARCHAR(50),
                         message NVARCHAR(MAX),
                         [time] DATETIME DEFAULT GETDATE()
);
CREATE TABLE joinedchat (
                            user_id NVARCHAR(50) FOREIGN KEY REFERENCES [user],
                            chat_id NVARCHAR(50)
);
CREATE table chat(
    chat_id NVARCHAR(50) primary key,
    chat_name nvarchar(50)
)