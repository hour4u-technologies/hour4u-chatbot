package com.org.h4u.chatbot.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessagePayload implements Serializable {
	
	private static final long serialVersionUID = 8220865120393468890L;
	private String conversationId;
	private String rawMessage;
	private EmploymentInputMessage employmentInputMessage;
	private List<String> messageIds = new ArrayList<>();
	private String conversationStatus;
	private Date lastUpdatedDate;
	
	//private List<Message> chatHistory = new ArrayList<>();
	

}
