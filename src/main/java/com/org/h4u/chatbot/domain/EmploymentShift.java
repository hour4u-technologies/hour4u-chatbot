package com.org.h4u.chatbot.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class EmploymentShift {
	
	private String startTime; // in HH:MM format
	private String endTime;
	

}
