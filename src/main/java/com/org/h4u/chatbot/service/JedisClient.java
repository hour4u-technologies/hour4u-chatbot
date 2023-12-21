package com.org.h4u.chatbot.service;


import com.org.h4u.chatbot.domain.RedisMessagePayload;
import com.org.h4u.chatbot.domain.UserConversations;

public interface JedisClient {
	
	void storeMessage(String key, RedisMessagePayload message);
	
	void storeMessage(String key, UserConversations message);
	
	//RedisMessagePayload retrieveMessage(String key);
	
	UserConversations retrieveMessage(String key);

    /**
     * Check whether the given key exists.
     */
    Boolean exists(String key);

    
    /**
     * Delete a given key.
     */
    Long del(String key);
    
    void flushRedisCache();

}
