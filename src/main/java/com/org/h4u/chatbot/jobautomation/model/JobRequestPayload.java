package com.org.h4u.chatbot.jobautomation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobRequestPayload {
	
	private String employerName;
	private String jobTypeId;
	private String employerId;
	private String description;
	private String title;
	private String dressCodeFemale;
	private String dressCodeMale;
	private String prerequisites;
	
	
}
