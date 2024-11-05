package com.project.course.subscription.email;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.project.course.subscription.model.Subscription.SubscriptionType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService  {

	@Autowired
	private EmailRepository emailRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine templateEngine;

	// Method to send an email
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
	
	public void sendSubscriptionConfirmation(String to, String userName, String planName, Object object, LocalDateTime expiryDate, SubscriptionType subscriptionType) throws MessagingException {
	    String subject = "Your Subscription Has Been Activated!";
	    
	    String htmlTemplate = "SubscriptionConfirmation.html"; 
	    
	    String htmlContent = htmlTemplate

	        .replace("{{userName}}", userName)
	        .replace("{{planName}}", planName)
	        .replace("{{purchaseDate}}", object.toString())
	        .replace("{{expiryDate}}", expiryDate.toString())
	        .replace("{{subscriptionType}}", subscriptionType.toString());

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true);
	    
	    try {
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(htmlContent, true);
	        mailSender.send(message);
	    } catch (MessagingException e) {
	        // Handle the exception
	        e.printStackTrace(); // Log the exception or handle it as per your requirement
	    }
	}

	@Override
	public void sendConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
			LocalDateTime ExpiryTime, SubscriptionType subscriptionType,String htmlfile) {
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
	public void sendExpiryNotification(String to, String clientName, String PlanName, Object setPurchaseDate,
			LocalDateTime ExpiryDate, String htmlfile) {
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
			context.setVariable("expiryDate", ExpiryDate);

			// Process the HTML template with Thymeleaf
			String htmlContent = templateEngine.process(htmlfile, context);

			// Set the HTML content in the email body
			helper.setText(htmlContent, true); // true to indicate that it is HTML

			// Send the email
			mailSender.send(message);
			System.out.println("Registration email sent successfully!");

		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while sending email: " + e.getMessage());
		}
		
	}

	}

