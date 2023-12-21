package com.org.h4u.chatbot.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserConversations implements Serializable{

	private String userId;
	private List<String> messageIds = new ArrayList<>();
	private List<RedisMessagePayload> conversations = new ArrayList<>();
}
