package com.org.h4u.chatbot.jobautomation;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.org.h4u.chatbot.common.GeneralConstants;
import com.org.h4u.chatbot.jobautomation.model.AuthTokenResponse;

@Service
public class Hour4UAuthTokenService {
	
	@Value("${hour4u.api.token.url}")
    private String authTokenUrl;

    @Value("${hour4u.api.client_id}")
    private String clientId;

    @Value("${hour4u.api.client_secret}")
    private String clientSecret;
    
    @Value("${hour4u.api.username}")
    private String username;
    
    @Value("${hour4u.api.password}")
    private String password;
    
    public String getAuthToken() {
    	
    	AuthTokenResponse authTokenResponse = GeneralConstants.AUTH_TOKEN;
    	String authToken = null;
    	if(null==authTokenResponse || isTokenExpired(authTokenResponse.getTokenExpirationTime())) {
    		authToken = fetchAuthToken();
    	}else {
    		authToken = authTokenResponse.getAccess_token();
    	}
    	return authToken;
    }
    
    private String fetchAuthToken() {
        // Set up headers with client credentials
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);
        
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", username);
        requestBody.add("password", password);

        // Create an HTTP request entity with the headers
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Make a POST request to obtain the auth token
        ResponseEntity<AuthTokenResponse> responseEntity = new RestTemplate().exchange(
                authTokenUrl, HttpMethod.POST, requestEntity, AuthTokenResponse.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            AuthTokenResponse tokenResponse = responseEntity.getBody();
            tokenResponse.setTokenExpirationTime(calculateTokenExpiration(tokenResponse.getExpires_in()));
            GeneralConstants.AUTH_TOKEN = tokenResponse;
            return tokenResponse.getAccess_token();
        } else {
            // Handle error or throw an exception if token retrieval fails
            throw new RuntimeException("Unable to retrieve the authentication token");
        }
    }
    
    private boolean isTokenExpired(Long tokenExpirationTimestamp) {
        return System.currentTimeMillis() > tokenExpirationTimestamp;
    }

    private long calculateTokenExpiration(Long tokenLifetimeDurationMillis) {
        return System.currentTimeMillis() + tokenLifetimeDurationMillis; // Replace with actual value
    }

}
