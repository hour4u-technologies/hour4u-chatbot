package com.org.h4u.chatbot.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookMessagePayload {
	
	/** conversationId */
    private String conversationId;
    private String message;
    
}
