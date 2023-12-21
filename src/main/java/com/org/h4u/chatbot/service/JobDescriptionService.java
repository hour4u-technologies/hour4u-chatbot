package com.org.h4u.chatbot.service;

import com.org.h4u.chatbot.domain.UserMessage;

public interface JobDescriptionService {
	
	public String generateJobDescription(UserMessage inputMessage);

}
