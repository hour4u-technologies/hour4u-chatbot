package com.org.h4u.chatbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class UserMessage implements Serializable {
    /**
     * Chat content
     */
    private String text;
    
    private Integer responseWordLimit;
    
    private String aiResponse;
    
    public UserMessage(String text) {
    	this.text = text;
    }
    
    @Override
    public String toString() {
        return "text: " + text
                + ",\nresponseWordLimit: " + responseWordLimit
                + ",\naiResponse: " + aiResponse;
    }

}
