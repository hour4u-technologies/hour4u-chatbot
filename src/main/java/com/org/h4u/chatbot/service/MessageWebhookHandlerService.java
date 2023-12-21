package com.org.h4u.chatbot.service;

import com.org.h4u.chatbot.dto.WebhookEventRequestDto;
import com.org.h4u.chatbot.response.WebhookMessageResponse;

public interface MessageWebhookHandlerService {
	void handleWebhookEvent(WebhookEventRequestDto webhookEventRequestDto);
	void flushRedisCache();
}
