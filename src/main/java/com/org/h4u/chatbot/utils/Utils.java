package com.org.h4u.chatbot.utils;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.org.h4u.chatbot.common.GeneralConstants;
import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.domain.EmploymentShift;
import com.org.h4u.chatbot.domain.VolunteerDetails;

public class Utils {

	public static HttpHeaders getResponseHeader() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set(GeneralConstants.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
		responseHeaders.set("X-Frame-Options", "DENY");
		responseHeaders.set("X-Content-Type-Options", "nosniff");
		responseHeaders.set("Content-Security-Policy", "default-src 'none'");
		responseHeaders.set("Feature-Policy", "none");
		responseHeaders.set("Referrer-Policy", "no-referrer");
		responseHeaders.set("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
		responseHeaders.set("Cache-Control", "no-store");
		responseHeaders.set("Pragma", "no-cache");

		return responseHeaders;
	}

	public static String extractJsonString(String input) {
		input = removeNewLineCharacter(input);
		System.out.println("json after newline removal :: "+input);

		int begin = input.indexOf("{");
		System.out.println("begin :: "+begin);

		int end = input.lastIndexOf("}") + 1;
		System.out.println("end :: "+end);
		input = input.substring(begin, end);
		return input;
	}

	public static String removeNewLineCharacter(String input) {
		// Remove newline and tab characters
		return input.replaceAll("[\n\t]", " ");
	}
	
	public static boolean isWelcomeMessage(String message) {
		boolean result = false;
		if(message.toLowerCase().startsWith("hello") || message.toLowerCase().startsWith("hi") || message.toLowerCase().startsWith("hey")) {
			result = true;
		}
        return result;
    }

	public static EmploymentInputMessage parseJSONString(String jsonString) {

		// Create a JSON object from the JSON string
		EmploymentInputMessage employmentInputMessage = new EmploymentInputMessage();
		try {

			JSONObject employmentObject = new JSONObject(jsonString);
			employmentInputMessage.setEventName(employmentObject.getString(GeneralConstants.EVENT_NAME));
			employmentInputMessage.setClientName(employmentObject.getString(GeneralConstants.CLIENT_NAME));
			employmentInputMessage.setCity(employmentObject.getString(GeneralConstants.CITY));
			employmentInputMessage.setLocation(employmentObject.getString(GeneralConstants.LOCATION));
			employmentInputMessage.setEventDate(employmentObject.getString(GeneralConstants.EVENT_DATE));
			employmentInputMessage.setReportingTime(employmentObject.getString(GeneralConstants.REPORTING_TIME));
			employmentInputMessage.setJobDescription(employmentObject.getString(GeneralConstants.JOB_DESCRIPTION));
			try {
				Object clientBudgetObject = employmentObject.get(GeneralConstants.CLIENT_BUDGET);
				if(null!=clientBudgetObject) {
					if (clientBudgetObject instanceof String) {
						employmentInputMessage.setClientBudget(employmentObject.getString(GeneralConstants.CLIENT_BUDGET));
			        }

					else if (clientBudgetObject instanceof Integer) {
			        	employmentInputMessage.setClientBudget(Integer.toString(employmentObject.getInt(GeneralConstants.CLIENT_BUDGET)));
			            
			        }

					else if (clientBudgetObject instanceof Double) {
						employmentInputMessage.setClientBudget(Double.toString(employmentObject.getDouble(GeneralConstants.CLIENT_BUDGET)));
			        }
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			

			try {
				JSONArray shiftsJsonArray = employmentObject.getJSONArray(GeneralConstants.SHIFTS);
				if(null!=shiftsJsonArray && shiftsJsonArray.length()>0) {
					for(int i=0; i<shiftsJsonArray.length();i++) {
						EmploymentShift employmentShift = new EmploymentShift();
						JSONObject shiftJsonObject = shiftsJsonArray.getJSONObject(i);
						employmentShift.setStartTime(shiftJsonObject.getString(GeneralConstants.START_TIME));
						employmentShift.setEndTime(shiftJsonObject.getString(GeneralConstants.END_TIME));
						employmentInputMessage.getShifts().add(employmentShift);

					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			

			try {
				JSONArray volunteersJsonArray = employmentObject.getJSONArray(GeneralConstants.REQUIRED_VOLUNTEERS);
				if(null!=volunteersJsonArray && volunteersJsonArray.length()>0) {
					for(int i=0; i<volunteersJsonArray.length();i++) {
						JSONObject voluneteersJsonObject = volunteersJsonArray.getJSONObject(i);
						Map<String, VolunteerDetails> volunteerMap = new HashMap<>();

						VolunteerDetails volunteerDetails = new VolunteerDetails();
						volunteerDetails.setDressCode(voluneteersJsonObject.getString(GeneralConstants.DRESS_CODE));
						
						try {
							Object payPerShiftObject = voluneteersJsonObject.get(GeneralConstants.PAY_PER_SHIFT);
							if(null!=payPerShiftObject) {
								if (payPerShiftObject instanceof String) {
									volunteerDetails.setPayPerShift(voluneteersJsonObject.getString(GeneralConstants.PAY_PER_SHIFT));
						        }else if (payPerShiftObject instanceof Integer) {
						        	volunteerDetails.setPayPerShift(Integer.toString(voluneteersJsonObject.getInt(GeneralConstants.PAY_PER_SHIFT)));
						            
						        }else if (payPerShiftObject instanceof Double) {
						        	volunteerDetails.setPayPerShift(Double.toString(voluneteersJsonObject.getDouble(GeneralConstants.PAY_PER_SHIFT)));
						        }
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						try {
							Object travelAllowanceObject = voluneteersJsonObject.get(GeneralConstants.TRAVELLING_ALLOWANCE);
							if(null!=travelAllowanceObject) {
								if (travelAllowanceObject instanceof String) {
									volunteerDetails.setTravelAllowance(voluneteersJsonObject.getString(GeneralConstants.TRAVELLING_ALLOWANCE));
						        }else if (travelAllowanceObject instanceof Integer) {
						        	volunteerDetails.setTravelAllowance(Integer.toString(voluneteersJsonObject.getInt(GeneralConstants.TRAVELLING_ALLOWANCE)));
						            
						        }else if (travelAllowanceObject instanceof Double) {
						        	volunteerDetails.setTravelAllowance(Double.toString(voluneteersJsonObject.getDouble(GeneralConstants.TRAVELLING_ALLOWANCE)));
						        }
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						
						try {
							if(voluneteersJsonObject.has(GeneralConstants.MALE)) {
								Object maleVolunteerObject =  voluneteersJsonObject.get(GeneralConstants.MALE);
								if(null!=maleVolunteerObject) {
									if (maleVolunteerObject instanceof String) {
										volunteerDetails.setCount(voluneteersJsonObject.getString(GeneralConstants.MALE));
							        }else if (maleVolunteerObject instanceof Integer) {
							        	volunteerDetails.setCount(Integer.toString(voluneteersJsonObject.getInt(GeneralConstants.MALE)));
							            
							        }else if (maleVolunteerObject instanceof Double) {
							        	volunteerDetails.setCount(Double.toString(voluneteersJsonObject.getDouble(GeneralConstants.MALE)));
							        }
									volunteerDetails.setGender(GeneralConstants.MALE);
									volunteerMap.put(GeneralConstants.MALE, volunteerDetails);
								}
							}
							
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						try {
							if(voluneteersJsonObject.has(GeneralConstants.FEMALE)) {
								Object femaleVolunteerObject =  voluneteersJsonObject.get(GeneralConstants.FEMALE);
								if(null!=femaleVolunteerObject) {
									if (femaleVolunteerObject instanceof String) {
										volunteerDetails.setCount(voluneteersJsonObject.getString(GeneralConstants.FEMALE));
							        }else if (femaleVolunteerObject instanceof Integer) {
							        	volunteerDetails.setCount(Integer.toString(voluneteersJsonObject.getInt(GeneralConstants.FEMALE)));
							            
							        }else if (femaleVolunteerObject instanceof Double) {
							        	volunteerDetails.setCount(Double.toString(voluneteersJsonObject.getDouble(GeneralConstants.FEMALE)));
							        }
									volunteerDetails.setGender(GeneralConstants.FEMALE);
									volunteerMap.put(GeneralConstants.FEMALE, volunteerDetails);
								}
							}
							
						}catch(Exception e) {
							e.printStackTrace();
						}
						
						employmentInputMessage.getRequiredVolunteers().add(volunteerMap);
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}

			
			JSONArray prerequisiteJsonArray = employmentObject.getJSONArray(GeneralConstants.PREREQUISITES);
			if(null!=prerequisiteJsonArray && prerequisiteJsonArray.length()>0) {
				for(int i=0; i<prerequisiteJsonArray.length();i++) {
					employmentInputMessage.getPrerequisites().add(prerequisiteJsonArray.getString(i));
				}
			}
			employmentInputMessage.setClosingMessage(employmentObject.getString(GeneralConstants.CLOSING_MESSAGE));
			employmentInputMessage.setMissingInformationQuestion(employmentObject.getString(GeneralConstants.MISSING_INFORMATION_QUESTION));
			

		}catch(Exception e) {
			e.printStackTrace();
		}

		return employmentInputMessage;
	}
	
	public static String parseJobTypeJSONString(String jsonString) {
		String jobType = "";
		
		try {
			JSONObject jobTypeObject = new JSONObject(jsonString);
			jobType = jobTypeObject.getString(GeneralConstants.JOB_TYPE);
		}catch(Exception e) {
			e.printStackTrace();
		}	
		return jobType;
	}
	
	public static Map<String, String> parseClosestMatchingJobTypeJSONString(String jsonString) {
		Map<String, String> closestMatchJobType = new HashMap<>();
		
		try {
			JSONObject jobTypeObject = new JSONObject(jsonString);
			closestMatchJobType.put("id", jobTypeObject.getString("id"));
			closestMatchJobType.put("name", jobTypeObject.getString("name"));
			closestMatchJobType.put("notes", jobTypeObject.getString("description"));
		}catch(Exception e) {
			e.printStackTrace();
		}	
		return closestMatchJobType;
	}

}
