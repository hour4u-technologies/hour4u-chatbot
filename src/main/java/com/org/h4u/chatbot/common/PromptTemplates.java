package com.org.h4u.chatbot.common;

public class PromptTemplates {
	
	public static final String GENERATE_JOBDESCRIPTION_TEMPLATE = "You are an employer. Generate job description for below details. Mention mandatory details in job title. The response should not be more than WORD_COUNT words. In response do not mention about total word count or position summary/title details. Response should not have any heading.";
	
	/*public static final String RAW_EMPLOYMENT_DETAILS_PREFIX = "I have a webapplication which provides a platform to job seekers and employers to post job and job seekers can apply for it. Sometimes employers gave requirements in messages as well and our backend team extract the information from those messages and create the jobs in platform. Can you read and create the response in JSON format for the below content. Please assist me in generating a JSON response based on the following guidelines:\n"
			+ "\n"
			+ "1. The event date should be specific date only in yyyy-mm-dd format. If event date is missing or blank, ask for event date in 'missingInformationQuestion' attribute. \n"
			+ "2. If shift time is not mentioned in the input, then consider the reporting time as shift time. If shift time is missing or blank, ask for shift time in 'missingInformationQuestion' attribute. \n"
			+ "3. If male/female, man/women or boy/girl is not specified, do not provide it in the reponse. \n"
			+ "4. In requiredVolunteers, under male/female just mention the total number or count (numerical number) of required volunteers nothing else. \n"
			+ "5. If travelling allowance is mentioned in the pay then mention it in travellingAllowance field (numerical value only). \n"
			+ "6. The pay or amount to be paid will come under under 'payPerShift' and it should be just mathematical number/value no other alphabetical text. \n"
			+ "7. The payPerShift should be greater than zero. If payPerShift is missing or blank, ask for payPerShift in 'missingInformationQuestion' attribute. \n"
			+ "8. Any details related to job or job description should come under 'jobdescription' attribute in json response. \n"
			+ "9. Any details related to prerequistes for the job should come under 'prerequisites' section. \n"
			+ "10. The clientName field should not have 'client' as value in json response. \n"
			+ "11. The JSON response must have all the attributes as provided in below structure. \n"
			+ "12. If value of any attribute is not provided, leave it as blank.\n"
			+ "13. If all mandatory information is available then generate a closing message that says thanks to the user for providing the information and telling them we will generate the employement on their behalf and find the right candidates for the job. \n"
			+ "14. The closing message should be inside 'closingMessage' attribute if all mandatory information is available otherwise leave it as blank. \n"
			+ "15. You must not drop/delete any attribute from required json structure.\n"
			+ "16. Do not append any extra text in response apart from JSON. \n"
			+ "\n"
			+ "\n"
			+ "Mandatory Attributes: Below attributes are mandatory so if any attribute is missing or blank, then create a question asking the missing mandatory information (one at a time) in the 'missingInformationQuestion' attribute of JSON response.. You must not send the closing message unless all mandatory attributes has the values.\n"
			+ "jobDescription\n"
			+ "location\n"
			+ "eventDate\n"
			+ "shift timings\n"
			+ "volunteer details (male/female)\n"
			+ "pay per shift"; */
	
	public static final String REQUIREMENT_PROMPT_PREFIX = "Here's the input:\n";
	
	public static final String REQUIREMENT_WELCOME_MESSAGE_PREFIX = "My users who are the employers want to post a job/event requirements. So please respond to his '";
	
	public static final String REQUIREMENT_WELCOME_MESSAGE_SUFFIX =  "' message and welcome him to my platform 'Hour4U.com' and assume that you are Anmol and ask my user how you can assist him with his event requirements,  when is the event, shift time and pay per shift?";
	
	
	
	/*public static final String RAW_EMPLOYMENT_DETAILS_PREFIX = "Assume you are a platform provider where employers can post job requirements, and job seekers can apply for them. Employers can provide job details via chat. You, as the chatbot, will assist employers with gathering mandatory details. To set the context, you can welcome the employer with a message:\n"
			+ "\n"
			+ "\"Hello! Welcome to Hour4U.com, I'm Anmol and I'm here to assist you with your event requirements.\" \n"
			+ "\n"
			+ "There are certain mandatory details you must collect from employers, including:\n"
			+ "1. Job description\n"
			+ "2. Location of the event\n"
			+ "3. Event date\n"
			+ "4. Shift timings\n"
			+ "5. Number of male and female volunteers required\n"
			+ "6. Pay per shift (to be paid to job seekers)";*/
	
	public static final String RAW_EMPLOYMENT_DETAILS_PREFIX = "Assume you are a platform provider where employers can post job requirements, and job seekers can apply for them. Employers can provide job details via chat. You have to read the conversation and extract the required information in the JSON format. There are certain mandatory details you must collect from employers, including:\n"
			+ "1. Job description\n"
			+ "2. Location of the event\n"
			+ "3. Event date\n"
			+ "4. Shift timings\n"
			+ "5. Number of male and female volunteers required\n"
			+ "6. Pay per shift (to be paid to job seekers)";
	
	public static final String RAW_EMPLOYMENT_CHAT_HISTORY_PREFIX = "Chat History between you (Chatgpt) and employer:\n";
	
	/*public static final String RAW_EMPLOYMENT_GUIDELINES = "While responding to the employer's messages, you should ask relevant questions to obtain the missing mandatory details inside \"missingInformationQuestion\" field of the expected JSON format. Ask only one question at a time related to missing mandatory details from employer. You must respond in the expected JSON response structure only. Please assist me in generating a JSON response following these guidelines. You must follow the below guidelines :\n"
			+ "\n"
			+ "1. Event date should be in yyyy-mm-dd format. If it's missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "2. If shift time is not specified, consider the reporting time as the shift start time. The shift start time and endtime can not be same. If shift time is missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "3. Male/female, man/women, or boy/girl details should not be provided if not specified. Ask specifically about the male and females required.\n"
			+ "4. In 'requiredVolunteers,' under male/female, mention only the total count of required volunteers (numerical value).\n"
			+ "5. If traveling allowance is mentioned in the pay, put it in the 'travellingAllowance' field (numerical value only).\n"
			+ "6. 'PayPerShift' should be just a numerical value greater than zero. If it's missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "7. Job description details should be under the 'jobDescription' attribute in the JSON response.\n"
			+ "8. Prerequisites for the job should be in the 'prerequisites' section.\n"
			+ "9. The 'clientName' field should not have 'client' as the value in the JSON response.\n"
			+ "10. The JSON response must include all the attributes as provided in the given structure.\n"
			+ "11. Do not append any extra text in the response apart from JSON.\n"
			+ "12. If all mandatory information is available then generate a closing message that says thanks to the user for providing the information and telling them we will generate the employement on their behalf and find the right candidates for the job. \n"
			+ "13. The closing message should be inside 'closingMessage' attribute if all mandatory information is available otherwise leave it as blank.\n"
			+ "14. You must not remove any attribute from the given JSON structure while responding.\n"
			+ "15. You must not assume anything from your side.\n\n"; */
	
	/*public static final String RAW_EMPLOYMENT_GUIDELINES = "You should read the chat history and extract the information and generate the final response in expected JSON structure. You must respond in the expected JSON response structure only. Please assist me in generating a JSON response following these guidelines. You must follow the below guidelines :\n"
			+ "\n"
			+ "1. Event date should be in yyyy-mm-dd format. If it's missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "2. If shift time is not specified, consider the reporting time as the shift start time. The shift start time and endtime can not be same. If shift time is missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "3. Male/female, man/women, or boy/girl details should not be provided if not specified. Ask specifically about the male and females required.\n"
			+ "4. In 'requiredVolunteers,' under male/female, mention only the total count of required volunteers (numerical value).\n"
			+ "5. If traveling allowance is mentioned in the pay, put it in the 'travellingAllowance' field (numerical value only).\n"
			+ "6. 'PayPerShift' should be just a numerical value greater than zero. If it's missing or blank, ask for it in the 'missingInformationQuestion' attribute.\n"
			+ "7. Job description details should be under the 'jobDescription' attribute in the JSON response.\n"
			+ "8. Prerequisites for the job should be in the 'prerequisites' section.\n"
			+ "9. The 'clientName' field should not have 'client' as the value in the JSON response.\n"
			+ "10. The JSON response must include all the attributes as provided in the given structure.\n"
			+ "11. Do not append any extra text in the response apart from JSON.\n"
			+ "12. If all mandatory information is available then generate a closing message that says thanks to the user for providing the information and telling them we will generate the employement on their behalf and find the right candidates for the job. \n"
			+ "13. The closing message should be inside 'closingMessage' attribute if all mandatory information is available otherwise leave it as blank.\n"
			+ "14. You must not remove any attribute from the given JSON structure while responding.\n"
			+ "15. You must not assume anything from your side.\n\n";*/
	
	public static final String RAW_EMPLOYMENT_GUIDELINES = "You should read the chat history and extract the information and generate the final response in expected JSON structure. You must respond in the expected JSON response structure only. Please assist me in generating a JSON response following these guidelines. You must follow the below guidelines :\n"
			+ "\n"
			+ "1. eventDate should be in dd/mm format. You must not assume the year in eventDate as past year in case year is not provided by employer. Event date must be future date.\n"
			+ "2. If shift time is not specified, leave it blank. "
			+ "3. In case employer has provided the reporting time but not shift start time, you can consider reporting time as the shift start time. You must not assume the shift start time and end time if not specified by employer.\n"
			+ "4. If Male/female, man/women, or boy/girl details are not speificically provided, you must not assume it.\n"
			+ "5. In 'requiredVolunteers,' under male/female, mention only the total count of required volunteers (numerical value).\n"
			+ "6. If traveling allowance is mentioned in the pay, put it in the 'travellingAllowance' field (numerical value only).\n"
			+ "7. 'PayPerShift' should be just a numerical value greater than zero.\n"
			+ "8. Job description details should be under the 'jobDescription' attribute in the JSON response.\n"
			+ "9. Prerequisites for the job should be in the 'prerequisites' section.\n"
			+ "10. The 'clientName' field should not have 'client' as the value in the JSON response. You must not assume anything as client name.\n"
			+ "11. The JSON response must include all the attributes as provided in the given structure.\n"
			+ "12. Do not append any extra text in the response apart from JSON.\n"
			+ "13. You must not remove any attribute from the given JSON structure while responding.\n"
			+ "14. You must not assume anything from your side.";
	
	public static final String RAW_EMPLOYMENT_DETAILS_SUFFIX = "Expected JSON Response Structure.\n"
			+ "\n"
			+ " {\n"
			+ "   \"eventName\": \"The name of the event\",\n"
			+ "   \"clientName\": \"Client name who is organising the event.\",\n"
			+ "   \"city\": \"City where event will occur\",\n"
			+ "   \"location\": \"Specific location of the event\",\n"
			+ "   \"eventDate\": \"dd/mm format\",\n"
			+ "   \"shifts\":[\n"
			+ "   		{\n"
			+ "	    \"startTime\": \"HH:mm format\",\n"
			+ "	    \"endTime\": \"HH:mm format\"\n"
			+ "		}\n"
			+ "   ],\n"
			+ "   \"reportingTime\": \"HH:mm format\",\n"
			+ "   \"requiredVolunteers\": [\n"
			+ "     {\n"
			+ "       \"male\": \"\",\n"
			+ "       \"dressCode\": \"\",\n"
			+ "	   \"payPerShift\": \"\",\n"
			+ "	   \"travellingAllowance\": \"\"\n"
			+ "     },\n"
			+ "     {\n"
			+ "       \"female\": \"\",\n"
			+ "       \"dressCode\": \"\",\n"
			+ "	   \"payPerShift\": \"\",\n"
			+ "	   \"travellingAllowance\": \"\"\n"
			+ "     }\n"
			+ "   ],\n"
			+ "   \"jobDescription\" : \"\",\n"
			+ "   \"clientBudget\": \"\",\n"
			+ "   \"prerequisites\": [\n"
			+ "     \"\"\n"
			+ "   ],\n"
			+ "   \"missingInformationQuestion\": \"\",\n"
			+ "   \"closingMessage\": \"\""
			+ " }";
	
	public static final String ASK_USER_EVENT_LOCATION_PROMPT = "What is the location of the event?";
	public static final String ASK_USER_TYPE_OF_WORK = "What type of job do you need the workers to perform?";
	public static final String ASK_USER_EVENT_DATE_PROMPT = "Can you please tell me the specific date of event?";
	public static final String ASK_USER_EVENT_TIME_PROMPT = "Can you please tell me the specific start time and end time of the event?";
	public static final String ASK_USER_EVENT_ENDTIME_PROMPT = "Can you please tell me the end time of the event?";
	public static final String ASK_USER_REQUIRED_VOLUNTEERS = "Tell me how many men and women you need to help.";
	public static final String ASK_USER_BUDGET = "What is your budget per person? Any travelling allowance you will offer?";
	public static final String CLOSING_MESSAGE = "Thank you for providing all the details. We will create your employment details and find the right candidates for the job. Have a great day!";
	
	//Get Job Type from description
	public static final String JOB_TYPE_PREFIX_PROMPT = "I have a webapplication which provides a platform to job seekers and employers to post job and job seekers can apply for it. The employers gave requirements and provided the below job description. Can you please tell the type of job in 2-3 words. Provide the job type response in given JSON format. Please assist me in generating a JSON response.\n\n";
	public static final String JOB_DESC_PREFIX_PROMPT = "Input Job Description - ";
	public static final String JOB_TYPE_JSON_PROMPT = "\n\nRequired JSON Format - {\"jobType\": \"\"}";
	
	// Matching Job Type Prompts
	public static final String MATCHING_JOB_TYPE_PREFIX_PROMPT = "I have a set of job types collection in which I have job type Id, name and its description. The collections is provided below. Can you check and identify the closest matching record from the given collection for a given input job type. Provide the response in given JSON format only and no other text. Please assist me in generating a JSON response and do not write the code.\n\n";
	public static final String MATCHING_JOB_COLLECTION_PROMPT = "Job Types Collection - \n";
	public static final String MATCHING_JOB_INPUT_JOB_TYPE_PROMPT = "Input Job Type - ";
	public static final String MATCHING_JOB_TYPE_JSON_PROMPT = "\n\nRequried Json Format - \n"
			+ "{\"id\": \"\", \"name\": \"\", description: \"\"}";
	
	
	
}
