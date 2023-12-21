package com.org.h4u.chatbot.jobautomation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.h4u.chatbot.common.GeneralConstants;
import com.org.h4u.chatbot.common.PromptTemplates;
import com.org.h4u.chatbot.jobautomation.model.ChatGptMatchingJobTypePayload;
import com.org.h4u.chatbot.jobautomation.model.Employer;
import com.org.h4u.chatbot.jobautomation.model.Job;
import com.org.h4u.chatbot.jobautomation.model.JobRequestPayload;
import com.org.h4u.chatbot.jobautomation.model.JobType;
import com.org.h4u.chatbot.response.ChatResponse;
import com.org.h4u.chatbot.service.GenAIClientService;
import com.org.h4u.chatbot.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Hour4UJobService {
	
	/*@Value("${hour4U.api.jobTypeAPIEndpoint}")
    private String jobTypeAPIEndpoint;
	
	@Value("${hour4U.api.employersAPIEndpoint}")
    private String employersAPIEndpoint;*/
	
	@Value("${services.auth.url}")
    private String authServiceURL;
	
	@Value("${services.job-service.url}")
    private String jobServiceURL;
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Autowired
	private Hour4UAuthTokenService hour4uAuthTokenService;
	
	@Autowired
	private GenAIClientService genAIClientService;
	
	public JobType getJobTypes(String jobDescription) {
		log.info("Getting the job types...");
		
		JobType finalJobType = null;
		// Use chatgpt to get short job type from job description.
		String prompt = "";
		prompt = PromptTemplates.JOB_TYPE_PREFIX_PROMPT+PromptTemplates.JOB_DESC_PREFIX_PROMPT+jobDescription+PromptTemplates.JOB_TYPE_JSON_PROMPT;
		ChatResponse chatGptResponseObj = genAIClientService.getChatGptResponse(prompt);
		
		String chatGptResponse = chatGptResponseObj.getAiResponse();
		log.info("chatGptResponse :: "+chatGptResponse);
		String responseJsonString = Utils.extractJsonString(chatGptResponse);
		log.debug("Json String :: "+responseJsonString);
		String identifiedJobType = Utils.parseJobTypeJSONString(responseJsonString);
		
		// Get the matching jobtypes from database
		HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hour4uAuthTokenService.getAuthToken());
		String providerApiUrl = jobServiceURL+GeneralConstants.SEARCH_JOB_TYPE_API_ENDPOINT;
		
		headers.add("Content-Type", "application/json");
		
		List<String> keywords = Arrays.asList(identifiedJobType.split(" "));

        // Create a request entity with headers and keywords
        HttpEntity<List<String>> requestEntity = new HttpEntity<>(keywords, headers);
        
        // Create a request to the provider API
        ResponseEntity<List<JobType>> responseEntity = restTemplate.exchange(
            providerApiUrl,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<JobType>>() {}
        );
        
        List<JobType> matchingJobTypes = new ArrayList<>();
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
        	matchingJobTypes = responseEntity.getBody();
        }else {
        	log.error("No matching job type found...");
        }
        
        List<ChatGptMatchingJobTypePayload> chatGptMatchingJobTypePayloads = new ArrayList<>();
        
        for (JobType matchingJobType : matchingJobTypes) {
			ChatGptMatchingJobTypePayload chatGptMatchingJobTypePayload = new ChatGptMatchingJobTypePayload();
			chatGptMatchingJobTypePayload.setId(matchingJobType.getId());
			chatGptMatchingJobTypePayload.setName(matchingJobType.getName());
			if(null!=matchingJobType.getNotes() && !matchingJobType.getNotes().equals("")) {
				chatGptMatchingJobTypePayload.setDescription(matchingJobType.getNotes());
				chatGptMatchingJobTypePayloads.add(chatGptMatchingJobTypePayload);
				if(chatGptMatchingJobTypePayloads.size()>=10) {
					break;
				}
				
			}
			
		}
        String chatGptMatchingJobTypePayloadsString = "";
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the list to a JSON string
        	chatGptMatchingJobTypePayloadsString = objectMapper.writeValueAsString(chatGptMatchingJobTypePayloads);
        } catch (Exception e) {
            e.printStackTrace();
        }
		
        if(!chatGptMatchingJobTypePayloadsString.equals("")) {
        	
        	// call chatgpt service to find closest match job type
        	prompt = PromptTemplates.MATCHING_JOB_TYPE_PREFIX_PROMPT+PromptTemplates.MATCHING_JOB_COLLECTION_PROMPT+
    				chatGptMatchingJobTypePayloadsString+PromptTemplates.MATCHING_JOB_INPUT_JOB_TYPE_PROMPT+identifiedJobType+PromptTemplates.MATCHING_JOB_TYPE_JSON_PROMPT;
    		ChatResponse chatGptClosestJobTypeResponseObj = genAIClientService.getChatGptResponse(prompt);
    		String chatGptClosestJobTypeResponse = chatGptClosestJobTypeResponseObj.getAiResponse();
    		log.info("chatGptClosestJobTypeResponse :: "+chatGptClosestJobTypeResponse);
    		String chatGptClosestJobTypeResponseString = Utils.extractJsonString(chatGptClosestJobTypeResponse);
    		log.debug("chatGptClosestJobTypeResponseString Json String :: "+chatGptClosestJobTypeResponseString);
    		Map<String, String> closestMatchJobType = Utils.parseClosestMatchingJobTypeJSONString(chatGptClosestJobTypeResponseString);
    		
    		if(closestMatchJobType.size()>0) {
    			for (JobType matchingJobType : matchingJobTypes) {
    				if(matchingJobType.getId().equals(closestMatchJobType.get("id"))) {
    					finalJobType = matchingJobType;
    				}
    			}
    		}
        	
        }
        return finalJobType;
        
    }
	
	public Employer getEmployer(String contactNumber) {
		log.info("Fetching the employer details for :: "+contactNumber);
		Employer employer = null;
        String endpoint = authServiceURL + "?contactNumber=" + contactNumber;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hour4uAuthTokenService.getAuthToken());
        
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List<Employer>> responseEntity = restTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            requestEntity,
            new ParameterizedTypeReference<List<Employer>>(){}
        );
        
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
        	List<Employer> employers = responseEntity.getBody();
        	if(null!=employers && employers.size()>0)
        	employer = employers.get(0);
        } else {
            log.error("No employer found...");
        }
        return employer;
    }
	
	public Job createJob(JobRequestPayload jobRequestPayload) {
		log.info("Intiating the job create process...");
		Job job = null;
		String apiUrl = jobServiceURL+GeneralConstants.CREATE_JOB_API_ENDPOINT;

        // Authentication token
		HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hour4uAuthTokenService.getAuthToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a request entity with headers and payload
        HttpEntity<JobRequestPayload> requestEntity = new HttpEntity<>(jobRequestPayload, headers);

        // Make the POST request
        ResponseEntity<Job> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Job.class);

        // Check the response
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            job = responseEntity.getBody();
        } else {
            // Request failed
            log.error("Request failed with status code: " + responseEntity.getStatusCode());
        }
        return job;
	}
	
	/*public JobTypeResponse getEmployersList() {
		log.info("Getting the job types...");
		
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + hour4uAuthTokenService.getAuthToken());
        
        try {
        	RequestEntity<Void> requestEntity = RequestEntity
                    .get(new URI(employersAPIEndpoint))
                    .headers(headers)
                    .build();
            
            ResponseEntity<JobTypeResponse> responseEntity = restTemplate.exchange(
                requestEntity,
                JobTypeResponse.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
            	log.info("Got the job types...");
                return responseEntity.getBody();
            } else {
                throw new RuntimeException("Unable to retrieve data from the API");
            }
        }catch(Exception e) {
        	e.printStackTrace();
        	return null;
        	
        }
        
    }*/

}
