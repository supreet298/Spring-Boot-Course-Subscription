package com.project.course.subscription.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.course.subscription.email.EmailRepository;
import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.email.EmailSetting;
import com.project.course.subscription.model.PurchaseHistory;
import com.project.course.subscription.repository.PurchaseHistoryRepository;
import com.project.course.subscription.whatsapp.WhatsappRepository;
import com.project.course.subscription.whatsapp.WhatsappService;

@Service
public class NotificationService {
	@Autowired
	private PurchaseHistoryRepository purchaseHistoryRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private EmailRepository emailRepository;
	
	
	@Autowired
	private WhatsappService whatsappService;
	
	@Autowired WhatsappRepository whatsappRepository;
	
	// Schedule the job to run every two minutes (for testing purposes)
	// @Scheduled(cron = "0 0 8 * * ?") // Runs every day at 8 AM
    //	 @Scheduled(cron = "0 */2 * * * ?") // Runs every 2 minutes
	public void sendeNotification() {
		LocalDateTime now = LocalDateTime.now(); // Get the current time

		EmailSetting emailSetting = emailRepository.findById(1L)
				.orElseThrow(() -> new RuntimeException("Email setting not found"));
		Long renewalAlertDays = emailSetting.getRenewalDayAlert();

		// Define the threshold as 2 minutes from now
		LocalDateTime expiryThresholdStart = now;
		// LocalDateTime expiryThresholdEnd = now.plusDays(renewalAlertDays);
		LocalDateTime expiryThresholdEnd = now.plusMinutes(2);

		// Fetch plans expiring between now and 2 minutes from now
		List<PurchaseHistory> expiringPlans = purchaseHistoryRepository.findExpiringPlansBetween(expiryThresholdStart,
				expiryThresholdEnd);
		// Template for email notification
		String notificationFile = "expiry-notification.html";

		for (PurchaseHistory history : expiringPlans) {
			
			String type = history.getNotificationType();
			
			if (type.equals("EMAIL") || type.equals("BOTH"))
				emailService.sendExpiryNotification(
						history.getClientEmail(),
						history.getClientName(),
						history.getPlanName(),
						history.getPurchaseDate(),
						history.getExpiryDate(),
						notificationFile);
			System.out.println("Email sent to: " + history.getClientEmail());
			
			if(type.equals("WHATSAPP")||type.equals("BOTH"))
				whatsappService.sendWhatsAppMessage(
						history.getPaxUser().getPhoneNumber(),
						history.getPlanName()+" Your plan Will expiring soon please renewal to enjoy uninterupted services");
			
			System.out.println("MessageSent sent Sucessfully: " + history.getPaxUser().getPhoneNumber().toString());
		}
		}
	
}
