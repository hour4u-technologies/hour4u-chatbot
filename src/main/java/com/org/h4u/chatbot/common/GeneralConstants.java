package com.org.h4u.chatbot.common;

import com.org.h4u.chatbot.jobautomation.model.AuthTokenResponse;

public class GeneralConstants {

	//Logging Message
	public static final String INCOMING_REQUEST_LOG_MESSAGE = "Starting to process the incoming request";
	
	// ERROR MESSAGE
	public static final String ERROR_RESPONSE_FORMAT = "ErrorMessage: %s";
	public static final String REQUEST_ERROR_MESSAGE = "Error during processing request - %s";
	public static final String MISSING_PROPERTY_VALUE_ERROR_MESSAGE = "Missing property values - %s";
	public static final String MALFORMED_JSON_REQUEST = "Malformed JSON request";
	public static final String ERROR_MESSAGE = "Error Occurred";
	public static final String INVALID_JSON = "Invalid Json";
	
	// GENERAL 
	public static final String CONTENT_TYPE= "Content-Type";
	public static final String INBOUND_WEBHOOK_EVENTS = "message";
	public static final String WHATSAPP_CHANNEL = "whatsapp";
	
	
	public static final String EVENT_NAME = "eventName";
	public static final String CLIENT_NAME = "clientName";
	public static final String CITY = "city";
	public static final String LOCATION = "location";
	public static final String EVENT_DATE = "eventDate";
	public static final String REPORTING_TIME = "reportingTime";
	public static final String JOB_DESCRIPTION = "jobDescription";
	public static final String CLIENT_BUDGET = "clientBudget";
	public static final String SHIFTS = "shifts";
	public static final String START_TIME = "startTime";
	public static final String END_TIME = "endTime";
	public static final String REQUIRED_VOLUNTEERS = "requiredVolunteers";
	public static final String DRESS_CODE = "dressCode";
	public static final String PAY_PER_SHIFT = "payPerShift";
	public static final String TRAVELLING_ALLOWANCE = "travellingAllowance";
	
	public static final String MALE = "male";
	public static final String FEMALE = "female";
	public static final String JOB_TYPE = "jobType";

	public static final String PREREQUISITES = "prerequisites";
	
	public static final String MISSING_INFORMATION_QUESTION = "missingInformationQuestion";
	public static final String CLOSING_MESSAGE = "closingMessage";
	
	public static AuthTokenResponse AUTH_TOKEN = null;
	
	public static final String SEARCH_JOB_TYPE_API_ENDPOINT = "/v1/jobType/searchJobTypesByKeywords";
	
	public static final String CREATE_JOB_API_ENDPOINT = "/v1/job";

	
	private GeneralConstants() {}
}
