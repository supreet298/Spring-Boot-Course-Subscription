package com.project.course.subscription.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.service.PaymentService;
import com.stripe.model.checkout.Session;

@RestController
@RequestMapping("/webhook")
public class PaymentWebhookController {

	@Autowired
	private PaymentService paymentService;
//    @PostMapping("/stripe")
//    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload) {
//        // Parse the webhook event
//        Event event = Webhook.constructEvent(payload, sigHeader, "your_webhook_secret");
//        if ("payment_intent.succeeded".equals(event.getType())) {
//            // Update Purchase status to SUCCESS
//        }
//        return ResponseEntity.ok("Received");
//    }
	
//	@PostMapping("/create-intent")
//    public String createPaymentIntent(@RequestParam Double amount, @RequestParam String currency,@RequestParam String description) {
//        try {
//            return paymentService.createCheckoutSession(amount, currency, description, "localhost:8080/managePaxusers.html", "localhost:8080/managePaxHead.html");
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error creating payment intent.";
//        }
//    }
	
	@PostMapping("/MakePayment")
	public ResponseEntity<Object> checkOut(@RequestBody PurchaseSubscriptionDTO paymentRequest) {
	    try {
	        Session session = paymentService.createCheckoutSession(paymentRequest);
	        return ResponseEntity.ok(session);
	    } catch (Exception e) {
	        e.printStackTrace(); // Log the exception for debugging
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create checkout session");
	    }
	}

	
}
