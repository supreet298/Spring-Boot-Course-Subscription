package com.project.course.subscription.whatsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;

import jakarta.annotation.PostConstruct;
@Service
public class WhatsappConfig {

	@Autowired
	private WhatsappRepository whatsappRepository;

	@PostConstruct
	public void init() {
	    try {
	        WhatsappSetting properties = whatsappRepository.findById(1L)
	                .orElseThrow(() -> new RuntimeException("WhatsApp properties not found"));
	        Twilio.init(properties.getAccountSid(), properties.getAuthToken());
	    } catch (Exception e) {
	        // Log the error and provide a fallback
	        System.err.println("Failed to initialize Twilio: " + e.getMessage());
	        // Optionally set default values or handle gracefully
	    }
	}


	public String getFromNumber() {
		return whatsappRepository.findById(1L).get().getWhatsappNumber();
	}
}
