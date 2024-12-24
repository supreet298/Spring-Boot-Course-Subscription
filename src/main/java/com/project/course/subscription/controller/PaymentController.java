package com.project.course.subscription.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.course.subscription.email.EmailService;
import com.project.course.subscription.service.PurchaseSubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;

@RestController
public class PaymentController {
	@Autowired
	private PurchaseSubscriptionService purchaseSubscriptionService;
	private EmailService emailService;
	@GetMapping("/payment/success")
	public ResponseEntity<String> getSessionCheckOut(@RequestParam("session_id") String sessionId) {
		try {
			Session session = Session.retrieve(sessionId);
			if (session.getPaymentStatus().equalsIgnoreCase("PAID")) 
			{	
				System.out.println("/payment/success:executed and updated");
				purchaseSubscriptionService.updatepurchaseSubscriptionService(session);
				System.out.println();
;				return ResponseEntity.status(HttpStatus.CREATED).body(" payment was made successfully! ✅");
			}
			else
			{
				return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment is not completed ");
			}
		} catch (StripeException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
