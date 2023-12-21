package com.org.h4u.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.org.h4u.chatbot.dto.WebhookEventRequestDto;
import com.org.h4u.chatbot.response.WebhookMessageResponse;
import com.org.h4u.chatbot.service.MessageWebhookHandlerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/customai")
public class MessageWebhookController {
    @Autowired
    MessageWebhookHandlerService messageWebhookHandlerService;

    @PostMapping("/webhook/message")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> handleWebhookEvent(@RequestBody WebhookEventRequestDto webhookEventRequestDto){
        try {
        	messageWebhookHandlerService.handleWebhookEvent(webhookEventRequestDto);
        }catch(Exception e) {
        	e.printStackTrace();
        }
    	
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/deletecache")
    public void flushRedisCache() throws Exception {
        messageWebhookHandlerService.flushRedisCache();
    }
}

