package com.project.course.subscription.controller;

import com.project.course.subscription.model.PurchaseSubscription;
import com.project.course.subscription.repository.PurchaseSubscriptionRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeWebhookController {

    private static final String ENDPOINT_SECRET = "whsec_EdU4kC3YVOP9E9MFAnEdR979pceUP3bJ";
    
    @Autowired
    PurchaseSubscriptionRepository purchaseSubscriptionRepository;
    
    PurchaseSubscription purchaseSubscription = new PurchaseSubscription();

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeEvent(
        @RequestBody String payload,
        @RequestHeader("Stripe-Signature") String sigHeader) {

        try {
            // Verify the webhook signature
            Event event = Webhook.constructEvent(payload, sigHeader, ENDPOINT_SECRET);

            // Handle specific events
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session session = (Session) event.getDataObjectDeserializer()
                                                     .getObject()
                                                     .orElseThrow();
                    handleCheckoutSessionCompleted(session);
                    break;

                default:
                    System.out.println("Unhandled event type: " + event.getType());
            }
            return ResponseEntity.ok("Webhook received");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        // Extract details from the session
        String sessionId = session.getId();
        String paymentStatus = session.getPaymentStatus(); // e.g., "paid"
        String referenceId = session.getClientReferenceId(); // Optional custom ID
        String tranSactionId=session.getPaymentIntent();
        // Update your database
        System.out.println("Payment successful for session: " + sessionId);
        System.out.println("Payment refereceID: " + referenceId);
        System.out.println("Payment IntentId: " + tranSactionId);

        


      //  purchaseSubscription.setCost(session.getAmountTotal().);
      //  purchaseSubscription.setTransactionId(tranSactionId);
      //  purchaseSubscription.setPaymentStatus(paymentStatus.toString());
      //  purchaseSubscription.setPaid(true);
    
       // purchaseSubscriptionRepository.save(purchaseSubscription);
    }
}