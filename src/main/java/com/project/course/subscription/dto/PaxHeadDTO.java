package com.project.course.subscription.dto;

import lombok.Data;

@Data
public class PaxHeadDTO {
	
	private Long id;
	
	private String uuid;
	
    private String phoneNumber;
    
    private String countryCode;
    
    private String name;

    private String email;
    
}
