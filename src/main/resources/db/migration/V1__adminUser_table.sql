CREATE TABLE admin_user(
    id        bigint NOT NULL AUTO_INCREMENT,
    uuid      CHAR(36) DEFAULT (UUID()) NOT NULL,
    user_name varchar(255) NOT NULL,
    email     varchar(255) UNIQUE,
    password  varchar(255) NOT NULL,
    phone_number  bigint UNIQUE,
    address varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO admin_user (id,uuid,user_name,email,password,phone_number,address) VALUES
(1,UUID(),'admin','admin@gmail.com','$2a$12$OkcRRCO2Ou4pIOs2986m7Ov2MXbDWRrVL5gLq3vMr0XYu14rOt1aa',8765437658,'1234 Elm Street, Springfield, IL 62704');
