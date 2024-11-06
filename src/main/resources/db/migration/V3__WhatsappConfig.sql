CREATE TABLE IF NOT EXISTS whatsapp_setting( 
id BIGINT PRIMARY KEY AUTO_INCREMENT,
account_sid VARCHAR(256) NOT NULL,
auth_token VARCHAR(256) NOT NULL,
whatsapp_number VARCHAR(36) NOT NULL
);
INSERT INTO whatsapp_setting(id, account_sid, auth_token, whatsapp_number)
VALUES(NULL, '123ACe3f353e87089f2331701c1a83eee800f123', 'abcd2914bed4a9317fe77085ade11489f068abcd', '+14155238886');

