package com.project.course.subscription.dto;

import com.project.course.subscription.model.PaxUser;
import lombok.Data;

@Data
public class PaxHeadDTO {
	
	private String name;

    private String email;

	private String uuid;
	
    private String phoneNumber;
    
    private String country;
    
    private String address;
    
    private String countryCode;
    
    private PaxUser.Type type;
}