package com.org.h4u.chatbot.service;

import com.org.h4u.chatbot.response.ChatResponse;

public interface GenAIClientService {
	
	public ChatResponse getChatGptResponse(String prompt);

}
