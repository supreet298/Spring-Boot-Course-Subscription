package com.project.course.subscription.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.course.subscription.config.StripeConfig;
import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.project.course.subscription.model.Subscription;
import com.project.course.subscription.service.PaymentService;
import com.project.course.subscription.service.SubscriptionService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentServiceImpl implements PaymentService {
 
	@Autowired
	private StripeConfig stripeConfig;
	
	@Autowired
    private SubscriptionService subscriptionService;

	@Override
	public Session createCheckoutSession(PurchaseSubscriptionDTO paymentRequest) {
	    // Retrieve subscription details
	    Subscription subscription = subscriptionService.getSubscriptionByUuid(paymentRequest.getSubscriptionUuid());
	    if (subscription == null) {
	        throw new RuntimeException("Subscription not found for the given UUID.");
	    }

	    // Set Stripe API key (ideally in a one-time initialization block)
	    Stripe.apiKey = stripeConfig.getStripeApiKey();

	    try {
	        // Build product data
	        SessionCreateParams.LineItem.PriceData.ProductData productData =
	            SessionCreateParams.LineItem.PriceData.ProductData.builder()
	                .setName(subscription.getPlanName())
	                .build();

	        // Build price data
	        SessionCreateParams.LineItem.PriceData priceData =
	            SessionCreateParams.LineItem.PriceData.builder()
	                .setCurrency(paymentRequest.getCurrency() != null
	                    ? paymentRequest.getCurrency().toString()
	                    : "USD")
	                .setUnitAmount(subscription.getCost().longValue())
	                .setProductData(productData)
	                .build();

	        // Build line item
	        SessionCreateParams.LineItem lineItem =
	            SessionCreateParams.LineItem.builder()
	                .setQuantity(1L)
	                .setPriceData(priceData)
	                .build();

	        // Build the session
	        SessionCreateParams params = SessionCreateParams.builder()
	            .addPaymentMethodType(paymentRequest.getPaymentMethodType() != null
	                ? SessionCreateParams.PaymentMethodType.valueOf(paymentRequest.getPaymentMethodType().toString())
	                : SessionCreateParams.PaymentMethodType.CARD)
	            .setMode(paymentRequest.getSessionMode() != null
	                ? SessionCreateParams.Mode.valueOf(paymentRequest.getSessionMode().toString())
	                : SessionCreateParams.Mode.PAYMENT)
	            .setSuccessUrl("http://localhost:8080/payment/success?session_id={CHECKOUT_SESSION_ID}")
	            .setCancelUrl("http://localhost:8080/payment/cancel")
	            .addLineItem(lineItem)
	            .build();

	        // Create and return session
	        Session session = Session.create(params);
	      //  logger.info("Stripe Session created successfully: {}", session.getId());
	        return session;

	    } catch (StripeException e) {
	      //  logger.error("Stripe API error during session creation: ", e);
	        throw new RuntimeException("Failed to create Stripe Checkout Session. Please try again later.");
	    }
	}

}
