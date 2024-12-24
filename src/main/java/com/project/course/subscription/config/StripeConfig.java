package com.project.course.subscription.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;
import lombok.Getter;

@Configuration
@Getter
public class StripeConfig {
	
	@Value("${stripe.api.key}")
	private String stripeApiKey;
	
	 // Use @PostConstruct to initialize the Stripe API key
    @PostConstruct
    public void initStripeApiKey() {
        Stripe.apiKey = stripeApiKey;
    }

}
