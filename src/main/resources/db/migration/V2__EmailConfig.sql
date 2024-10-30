CREATE TABLE IF NOT EXISTS email_setting(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    smtp_auth BOOLEAN NOT NULL,
    starttls_enable BOOLEAN NOT NULL,
    renewal_day_alert BIGINT NOT NULL
);

INSERT INTO email_setting (host, port, user_name, password, smtp_auth, starttls_enable,renewal_day_alert) 
VALUES ('smtp.gmail.com', 587, 'karthikeyan190100@gmail.com', '12345ovqlyooqaqgcpyvd', TRUE, TRUE,5);
