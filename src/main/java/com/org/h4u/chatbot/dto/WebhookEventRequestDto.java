package com.org.h4u.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.h4u.chatbot.domain.WebhookMessagePayload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebhookEventRequestDto {
    
	private String eventType;
	private String conversationId;
	private String text;
	private String waId;
	//private String messageId;
	private String whatsappMessageId;
	// varaible to support in identifying retry message
	private String assignedId;
	
    //private WebhookMessagePayload webhookMessagePayload;

}
