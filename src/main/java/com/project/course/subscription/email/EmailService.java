package com.project.course.subscription.email;

import java.time.LocalDateTime;

import com.project.course.subscription.model.Subscription.SubscriptionType;

import jakarta.mail.MessagingException;

public interface EmailService {
	void sendEmail(String to, String subject, String text);

	void sendConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
			LocalDateTime ExpirayTime, SubscriptionType subscriptionType,String htmlfile);

	void sendExpiryNotification(String to,String clientName,String PlanName,Object PurchaseDate,
			LocalDateTime ExpirayDate,String htmlfile);
}