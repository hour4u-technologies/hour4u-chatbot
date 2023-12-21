package com.org.h4u.chatbot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.domain.RedisMessagePayload;
import com.org.h4u.chatbot.service.JedisClient;
import com.org.h4u.chatbot.service.impl.WhatsappMsgServiceApiHandler;

@SpringBootTest
public class WhatsAppServiceTest {

	@InjectMocks
	private JedisClient redisMessageService;

	
	@Mock
	private WhatsappMsgServiceApiHandler whatspMsgServiceApiHandler;

	@BeforeEach
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	/*@Test
	public void testProcessWhatsAppMessage() {
		// Arrange
		String whatsappMessage = "Hello";
		String chatGPTResponse = "Sample ChatGPT response";
		String redisKey = "65316943f45af8e0fe7fa1e12-samplekey";
		String targetPhoneNumber = "+919899692810";

		// Mock the behavior of ChatGPTService
		Mockito.when(chatGptService.getChatGptResponse(whatsappMessage)).thenReturn(chatGPTResponse);

		RedisMessagePayload redisMessagePayload = new RedisMessagePayload();
		redisMessagePayload.setConversationId(redisKey);
		redisMessagePayload.setRawMessage(whatsappMessage);
		redisMessagePayload.setEmploymentInputMessage(new EmploymentInputMessage());

		// Mock the behavior of Redis
		Mockito.doNothing().when(redisMessageService).storeMessage(redisKey, redisMessagePayload);

		// Act
		try {
			whatspMsgServiceApiHandler.postSessionMessage(targetPhoneNumber, "messageText", whatsappMessage);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Assert
		Mockito.verify(chatGptService).getChatGptResponse(whatsappMessage);
		Mockito.verify(redisMessageService).storeMessage(redisKey, redisMessagePayload);
		// Add more assertions based on the expected behavior of your service
	}
	
	@Test
	public void testRedisRetreiveHandling() {
		// Arrange
		String whatsappMessage = "Sample WhatsApp message";
		String chatGPTResponse = "Sample ChatGPT response";
		String redisKey = "sampleKey";

		// Mock the behavior of ChatGPTService
		Mockito.when(chatGptService.getChatGptResponse(whatsappMessage)).thenReturn(chatGPTResponse);
		
		RedisMessagePayload redisMessagePayload = new RedisMessagePayload();
		redisMessagePayload.setConversationId(redisKey);
		redisMessagePayload.setRawMessage(whatsappMessage);
		redisMessagePayload.setEmploymentInputMessage(new EmploymentInputMessage());

		// Mock the behavior of Redis to return null when an object is not found
		Mockito.doNothing().when(redisMessageService).storeMessage(redisKey, redisMessagePayload);
		
		Mockito.when(redisMessageService.retrieveMessage(redisKey)).thenReturn(redisMessagePayload);

		// Act
		RedisMessagePayload response = redisMessageService.retrieveMessage(redisKey);

	    // Assert the expected behavior
		assertNotNull(response);
	    assertEquals(redisMessagePayload, response); 
		
	}

	@Test
	public void testExceptionHandling() {
		// Arrange
		String whatsappMessage = "Invalid WhatsApp message";
		String chatGPTResponse = "Unable to identify the intent";
		String targetPhoneNumber = "+919899692810";

		// Mock the behavior of ChatGPTService to throw an exception
		Mockito.when(chatGptService.getChatGptResponse(whatsappMessage)).thenReturn(chatGPTResponse);

		// Act
		try {
			whatspMsgServiceApiHandler.postSessionMessage(targetPhoneNumber, "messageText", "How can I help you?");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testRedisErrorHandling() {
		// Arrange
		String whatsappMessage = "Sample WhatsApp message";
		String chatGPTResponse = "Sample ChatGPT response";
		String redisKey = "sampleKey";
		String invalidKey = "test-redis-key";

		// Mock the behavior of ChatGPTService
		Mockito.when(chatGptService.getChatGptResponse(whatsappMessage)).thenReturn(chatGPTResponse);
		
		RedisMessagePayload redisMessagePayload = new RedisMessagePayload();
		redisMessagePayload.setConversationId(redisKey);
		redisMessagePayload.setRawMessage(whatsappMessage);
		redisMessagePayload.setEmploymentInputMessage(new EmploymentInputMessage());

		// Mock the behavior of Redis to return null when an object is not found
		Mockito.doNothing().when(redisMessageService).storeMessage(redisKey, redisMessagePayload);
		
		Mockito.when(redisMessageService.retrieveMessage(invalidKey)).thenReturn(null);

		// Act
		RedisMessagePayload response = redisMessageService.retrieveMessage(invalidKey);

	    // Assert the expected behavior
	    assertNull(response);
		
	} */

}
