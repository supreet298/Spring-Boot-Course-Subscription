package com.project.course.subscription.dto;

import lombok.Data;

@Data
public class PaxMemberDTO {

	private String uuid;
	private String name;
	private String email;
	private String phoneNumber;
	private String headUuid;
	private String address;
	private String country;
	private String countryCode;
	private String type;
	private String relation;
}
