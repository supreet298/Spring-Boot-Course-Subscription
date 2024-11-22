package com.project.course.subscription.email;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.project.course.subscription.exception.ResourceNotFoundException;
import com.project.course.subscription.model.Subscription.SubscriptionType;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;

    // Method to send an email
    @Override
    public void sendEmail(String to, String subject, String text) {
        // Get email configuration from the database
        EmailSetting emailConfig = emailRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Email config not found"));

        // Create a MimeMessage for sending email
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailConfig.getUserName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

       
    @Override
    @Async
    public void sendPurchaseConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
								 LocalDate ExpiryTime, SubscriptionType subscriptionType,String paymentStatus, String htmlfile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Subscription Has Been Activated!");

            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("planName", planName);
            context.setVariable("setPurchaseDate", setPurchaseDate);
            context.setVariable("ExpiryTime", ExpiryTime);
            context.setVariable("subscriptionType", subscriptionType);
            context.setVariable("paymentStatus", paymentStatus);

            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process(htmlfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            System.out.println("Purchase Email Sent Successfully !");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }
    }
    
    @Async
    @Override
    public void sendExpiryNotification(String to, String clientName, String PlanName, LocalDate setPurchaseDate,
                                       LocalDate setExpiryDate, String htmlfile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Plan Expiry Alert: Renew Now to Continue Enjoying [Service/Product]");

            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("clientName", clientName);
            context.setVariable("planName", PlanName);
            context.setVariable("purchaseDate", setPurchaseDate);
            context.setVariable("expiryDate", setExpiryDate);

            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process(htmlfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            System.out.println("Expiry Notification email sent successfully!");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }

    }
    
    @Async
	public void sendRenewalEmail(String to, String userName, String planName, LocalDate setPurchaseDate,
			LocalDate setExpirayDate, SubscriptionType subscriptionType, String htmlfile) {
		
		try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Subscription Has Been Successfully Renewed!");

            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("planName", planName);
            context.setVariable("setPurchaseDate", setPurchaseDate);
            context.setVariable("setExpirayDate", setExpirayDate);
            context.setVariable("subscriptionType", subscriptionType);

            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process(htmlfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            System.out.println("Renewal Email Sent Successfully !");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }

	}

	@Async
	@Override
	public void sendAutoRenewalCancellationEmail(String to, String userName, String planName,LocalDate setExpirayDate,String htmlfile) {
		
		try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Subscription Auto-Renewal Has Been Cancelled");
            
            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("planName", planName);
            context.setVariable("setExpirayDate", setExpirayDate);
            String htmlContent = templateEngine.process(htmlfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            System.out.println("AutoRenewal Cancellation Email Sent Successfully !");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }
	}
	
	@Async
	@Override
	public void sendPlanExpiredEmail(String to, String userName, String planName, String subscriptionType,
			LocalDate purchaseDate, LocalDate expiryDate, String htmlfile) {
		String Expiredfile="PlanExpired.html";
		try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Subscription Expired!");
            
            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("planName", planName);
            context.setVariable("subscriptionType", subscriptionType);
            context.setVariable("purchaseDate", purchaseDate);
            context.setVariable("expiryDate", expiryDate);
            String htmlContent = templateEngine.process(Expiredfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            //System.out.println(" Plan Expired Email Sent Successfully !");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }
	}

	@Async
	@Override
	public void sendpaymentConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
			LocalDate setExpirayDate, SubscriptionType subscriptionType, String paymentStatus, LocalDate paymentDate,
			String htmlfile) {
		try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            // helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your Subscription Has Been Activated!");

            // Create the email body with user details and a custom message
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("planName", planName);
            context.setVariable("setPurchaseDate", setPurchaseDate);
            context.setVariable("ExpiryTime", setExpirayDate);
            context.setVariable("subscriptionType", subscriptionType);
            context.setVariable("paymentStatus", paymentStatus);
            context.setVariable("paymentDate",paymentDate);

            // Process the HTML template with Thymeleaf
            String htmlContent = templateEngine.process(htmlfile, context);

            // Set the HTML content in the email body
            helper.setText(htmlContent, true); // true to indicate that it is HTML

            // Send the email
            mailSender.send(message);
            System.out.println("Purchase Email Sent Successfully !");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email: " + e.getMessage());
        }
		
	}


	@Override
	public EmailSettingResponseDTO getEmailProperties() {
		EmailSetting emailProperties=emailRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("Email settings not found"));
		 EmailSettingResponseDTO response = new EmailSettingResponseDTO();
				 response.setHost(emailProperties.getHost());
				 response.setPort(emailProperties.getPort());
				 response.setUserName(emailProperties.getUserName());
				 response.setPassword(passwordEncoder.encode(emailProperties.getPassword()));
				 response.setProtocol(emailProperties.getProtocol());
				 response.setSmtpAuth(emailProperties.isSmtpAuth());
				 response.setStarttlsEnable(emailProperties.isStarttlsEnable());
				 response.setRenewalDayAlert(emailProperties.getRenewalDayAlert());
		        return response;
		 	}
	

	
	public EmailSetting updateEmailProperties(EmailSetting request)
	{
		EmailSetting setting=emailRepository.findById(1L).orElseThrow(()->new ResourceNotFoundException("Email id not found"));	
		setting.setUserName(request.getUserName());
		setting.setPassword(request.getPassword());
		setting.setHost(request.getHost());
		setting.setPort(request.getPort());
		setting.setRenewalDayAlert(request.getRenewalDayAlert());
		//setting.setSmtpAuth(false);
		//setting.setStarttlsEnable(false);
		emailRepository.save(setting);
		return setting;
	}

}

