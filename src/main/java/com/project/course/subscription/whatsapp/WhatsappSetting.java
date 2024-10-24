package com.project.course.subscription.whatsapp;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappSetting {
	
	@Id
	private Long id;
	private String accountSid;
	private String authToken;
	private String whatsappNumber;
}
