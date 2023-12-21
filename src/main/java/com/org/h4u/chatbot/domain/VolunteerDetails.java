package com.org.h4u.chatbot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDetails {
	
	private String gender;
	private String count;
	private String dressCode;
	private String payPerShift;
	private String travelAllowance;
}
