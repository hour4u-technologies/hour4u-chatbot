package com.org.h4u.chatbot.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.h4u.chatbot.domain.RedisMessagePayload;
import com.org.h4u.chatbot.domain.UserConversations;
import com.org.h4u.chatbot.service.JedisClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisMessageService implements JedisClient{
	
	@Autowired
    private JedisPool jedisPool;
	
	@Autowired
	private ObjectMapper objectMapper;

    public void storeMessage(String key, RedisMessagePayload message) {
    	try (Jedis jedis = jedisPool.getResource()) {
    		String json = objectMapper.writeValueAsString(message);
            jedis.set(key, json);
    	}catch(JsonProcessingException e) {
    		e.printStackTrace();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void storeMessage(String key, UserConversations message) {
    	try (Jedis jedis = jedisPool.getResource()) {
    		String json = objectMapper.writeValueAsString(message);
            jedis.set(key, json);
    	}catch(JsonProcessingException e) {
    		e.printStackTrace();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }

    /* public RedisMessagePayload retrieveMessage(String key) {
    	try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            return objectMapper.readValue(json, RedisMessagePayload.class);
        } catch (Exception e) {
            return null;
        }
    } */
    
    public UserConversations retrieveMessage(String key) {
    	try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            return objectMapper.readValue(json, UserConversations.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    public Long del(String key) {
    	System.out.println("deleting the key :: "+key);
    	Long result = 0L;
    	try (Jedis jedis = jedisPool.getResource()) {
    		result = jedis.del(key);
    		System.out.println("Del Result :: "+result);
    	} catch (Exception e) {
    		e.printStackTrace();
            return null;
        }
        return result;
    }
    
    public void flushRedisCache() {
    	try (Jedis jedis = jedisPool.getResource()) {
    		jedis.flushAll();
        } catch (Exception e) {
        }
    	
    }
    
    public Boolean exists(String key) {
    	try (Jedis jedis = jedisPool.getResource()) {
    		return jedis.exists(key);
    		
    	}catch (Exception e) {
    		return false;
        }
    }
}
