package com.org.h4u.chatbot.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentInputMessage {
	
	private String eventName;
	private String clientName;
	private String contactNumber;
	private String city;
	private String location;
	private String eventDate;
	private List<EmploymentShift> shifts = new ArrayList<>();
	private String reportingTime;
	private String jobDescription;
	private String clientBudget;
	private List<String> prerequisites = new ArrayList<>();;
	private List<Map<String, VolunteerDetails>> requiredVolunteers = new ArrayList<>();
	private String missingInformationQuestion;
	private String closingMessage;

}
