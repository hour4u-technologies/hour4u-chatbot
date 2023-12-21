package com.org.h4u.chatbot.service.impl;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.org.h4u.chatbot.jobautomation.Hour4UAuthTokenService;
import com.org.h4u.chatbot.request.ChatRequest;
import com.org.h4u.chatbot.response.ChatResponse;
import com.org.h4u.chatbot.service.GenAIClientService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GenAIClientServiceImpl implements GenAIClientService{
	
	@Autowired
	private Hour4UAuthTokenService hour4uAuthTokenService;
	
	@Value("${services.gai-service.url}")
    private String gaiServiceUrl;
	
	@Autowired
    private  RestTemplate restTemplate;
	
	public ChatResponse getChatGptResponse(String prompt) {
        
		ChatResponse chatResponse = new ChatResponse();
        try {
        	
        	String authorizationToken = hour4uAuthTokenService.getAuthToken();
        	// Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authorizationToken);
            
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setPrompt(prompt);
            
            String apiURL = gaiServiceUrl+"/v1/customai/chat";
            System.out.println("apiURL : "+apiURL);
            
            RequestEntity<Object> requestEntity = new RequestEntity<>(chatRequest, HttpMethod.POST, URI.create(apiURL));
            ResponseEntity<ChatResponse> responseEntity = restTemplate.exchange(requestEntity, ChatResponse.class);
            log.info("ChatGpt service  ResponseCode :{} , ResponseBody:{} ", responseEntity.getStatusCode(), responseEntity.getBody());
            chatResponse = responseEntity.getBody();  
       
        }catch(Exception ex) {
        	log.error("Error occurred while retreiving the orderId. ",ex);
        }
        return chatResponse;
    }

}
