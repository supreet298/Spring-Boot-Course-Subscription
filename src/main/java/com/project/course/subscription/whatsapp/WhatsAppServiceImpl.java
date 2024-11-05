package com.project.course.subscription.whatsapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service
public class WhatsAppServiceImpl implements WhatsappService{

	@Autowired
	private WhatsappConfig whatsappConfig;

//	public String sendWhatsAppMessage(String to, String body) {
//		Message message = Message
//				.creator(new PhoneNumber("whatsapp:" + to), 
//						 new PhoneNumber(whatsappConfig.getFromNumber()), body)
//				.create();
//
//		return message.getSid();
//	}
	
	public String sendWhatsAppMessage(String to, String body) {
	    PhoneNumber toNumber = new PhoneNumber("whatsapp:" + to); 
	    PhoneNumber fromNumber = new PhoneNumber("whatsapp:" + whatsappConfig.getFromNumber()); 
	    
	    System.out.println("From :"+fromNumber+" To :"+toNumber);	    
	    Message message = Message
	            .creator(toNumber, fromNumber, body)
	            .create();

	    return message.getSid();
	}

}
