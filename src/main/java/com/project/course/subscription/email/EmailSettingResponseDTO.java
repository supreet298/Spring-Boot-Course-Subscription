package com.project.course.subscription.email;

import lombok.Data;

@Data
public class EmailSettingResponseDTO {
	private String host;
	private int port;
	private String userName;
	private String password;
	private String protocol;
	private boolean smtpAuth;
	private boolean starttlsEnable;
	private long renewalDayAlert;
	// Omit the password field

}
