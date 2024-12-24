package com.project.course.subscription.service;

import com.project.course.subscription.dto.PurchaseSubscriptionDTO;
import com.stripe.model.checkout.Session;

public interface PaymentService {
	//String createPayment(Double cost,String currency,String description);

	//String createCheckoutSession(Double cost, String currency, String description, String successUrl, String cancelUrl);

	Session createCheckoutSession(PurchaseSubscriptionDTO paymentRequest);

}
