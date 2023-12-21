package com.org.h4u.chatbot.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class ChatResponse implements Serializable {
    /**
     * Chat content
     */
    private String prompt;
    
    private String aiResponse;
    
    public ChatResponse() {}
}
