package com.org.h4u.chatbot.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class WebhookMessageResponse {

	private String type;
	private String sourceChannel;
	private String conversationId;
	private String message;

}
