package com.project.course.subscription.email;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.project.course.subscription.model.Subscription.SubscriptionType;

public interface EmailService {
	void sendEmail(String to, String subject, String text);

	void sendConfirmEmail(String to, String userName, String planName, Object setPurchaseDate,
						  LocalDate ExpiryTime, SubscriptionType subscriptionType, String htmlfile);

	void sendExpiryNotification(String to,String clientName,String PlanName,Object PurchaseDate,
			LocalDateTime ExpiryDate,String htmlfile);
}