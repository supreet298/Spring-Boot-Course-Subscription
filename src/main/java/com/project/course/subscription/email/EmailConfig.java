package com.project.course.subscription.email;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfig {

    //private static final Logger logger = LoggerFactory.getLogger(MailConfig.class);

    @Autowired
    private EmailRepository emailRepository;

    @Bean
    public JavaMailSender getJavaMailSender() {
        // Retrieve the email configuration from the database
        EmailSetting emailConfig = emailRepository.findById(1L)
            .orElseThrow(() -> new RuntimeException("Email config not found for ID: 1"));

        // Create the mail sender
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailConfig.getHost());
        mailSender.setPort(emailConfig.getPort());
        mailSender.setUsername(emailConfig.getUserName());
        mailSender.setPassword(emailConfig.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", emailConfig.isSmtpAuth());
        props.put("mail.smtp.starttls.enable", emailConfig.isStarttlsEnable());
      //  props.put("mail.debug", "true"); // Enable debugging

        // Log the email configuration for debugging purposes
//        logger.info("Mail Configuration: Host = {}, Port = {}, Username = {}",
//                emailConfig.getHost(), emailConfig.getPort(), emailConfig.getUserName());

        return mailSender;
    }

}
