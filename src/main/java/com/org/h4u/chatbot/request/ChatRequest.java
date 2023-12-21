package com.org.h4u.chatbot.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRequest implements Serializable {
    /**
     * Chat content
     */
    private String prompt;
    
    private Integer responseWordLimit;
    
    private String aiResponse;
    
    public ChatRequest(String prompt) {
    	this.prompt = prompt;
    }
    
    @Override
    public String toString() {
        return "text: " + prompt
                + ",\nresponseWordLimit: " + responseWordLimit
                + ",\naiResponse: " + aiResponse;
    }

}
