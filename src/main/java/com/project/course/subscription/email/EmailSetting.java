package com.project.course.subscription.email;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailSetting {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	private String host;
	private int port;
	private String userName;
	private String password;
	private String protocol;
    private boolean smtpAuth;
    private boolean starttlsEnable;
    private long renewalDayAlert;
}                         
