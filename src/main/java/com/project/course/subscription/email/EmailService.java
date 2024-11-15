package com.project.course.subscription.email;

import java.time.LocalDate;

import com.project.course.subscription.model.Subscription.SubscriptionType;

public interface EmailService {
	void sendEmail(String to, String subject, String text);

	void sendPurchaseConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
						  LocalDate setExpirayDate, SubscriptionType subscriptionType, String htmlfile);

	void sendExpiryNotification(String to,String clientName,String PlanName,LocalDate setPurchaseDate,
			LocalDate setExpiryDate,String htmlfile);
	
	void sendRenewalEmail(String to, String userName, String planName, LocalDate setPurchaseDate,
			  LocalDate ExpiryTime, SubscriptionType subscriptionType, String htmlfile);
	
	void sendAutoRenewalCancellationEmail(String to, String userName, String planName,LocalDate setExpirayDate,String htmlfile);
	
}