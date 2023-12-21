package com.org.h4u.chatbot.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.h4u.chatbot.common.GeneralConstants;
import com.org.h4u.chatbot.common.PromptTemplates;
import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.domain.EmploymentShift;
import com.org.h4u.chatbot.domain.RedisMessagePayload;
import com.org.h4u.chatbot.domain.UserConversations;
import com.org.h4u.chatbot.domain.VolunteerDetails;
import com.org.h4u.chatbot.dto.WebhookEventRequestDto;
import com.org.h4u.chatbot.message.MqProducer;
import com.org.h4u.chatbot.response.ChatResponse;
import com.org.h4u.chatbot.response.WebhookMessageResponse;
import com.org.h4u.chatbot.service.GenAIClientService;
import com.org.h4u.chatbot.service.JedisClient;
import com.org.h4u.chatbot.service.MessageWebhookHandlerService;
import com.org.h4u.chatbot.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IncomingMessageHandlerServiceImpl implements MessageWebhookHandlerService {

	@Autowired
	JedisClient redisMessageService;

	@Autowired
	private GenAIClientService genAIClientService;

	@Autowired
	private MqProducer mqProducer;

	@Autowired
	private WhatsappMsgServiceApiHandler whatspMsgServiceApiHandler;
	

	@Override
	public void handleWebhookEvent(WebhookEventRequestDto webhookEventRequestDto){

		WebhookMessageResponse webhookMessageResponse = null;
		try {
			if (webhookEventRequestDto.getEventType().equals(GeneralConstants.INBOUND_WEBHOOK_EVENTS)) {
				log.info(">>> Received : Incoming message from whatsapp...");
				handleIncomingMessage(webhookEventRequestDto);
			} else {
				log.info("<--- Ignoring : Received message webhook ---> " + webhookEventRequestDto.getEventType());
			}
		} catch (Exception e) {
			log.error("Failed to process webhook  " + e.getMessage());
		}
		//return webhookMessageResponse;
	}

	public void handleIncomingMessage(WebhookEventRequestDto webhookEventRequestDto) {
		log.info("Start processing the incoming webhook message...");
		String responseMessage = "";
		try {
			EmploymentInputMessage employmentInputMessage = null;
			String prompt = "";
			RedisMessagePayload redisMessagePayload = null;

			// Identify the welcome message
			if(null==webhookEventRequestDto.getText() || webhookEventRequestDto.getText().trim().equals("")) {
				log.info("Empty message so not processing it further...");
				return;
			}
			log.info("Input message :: "+webhookEventRequestDto.getText());
			log.info("Conversation Id :: "+webhookEventRequestDto.getConversationId());
			log.info("Message Id :: "+webhookEventRequestDto.getWhatsappMessageId());
			
			UserConversations userConversations = getUserConversationHistory(webhookEventRequestDto);
			boolean duplicateMessage = isDuplicateMessage(userConversations, webhookEventRequestDto);
			if(!duplicateMessage) {
				redisMessagePayload = getRedisMessagePayload(userConversations, webhookEventRequestDto);
				
				if(Utils.isWelcomeMessage(webhookEventRequestDto.getText())) {
					responseMessage = "Hello! Welcome to Hour4U.com, I'm Anmol and I'm here to assist you with your event requirements.";
					String historyMessage = "Employer : Hi\nChatGpt : Hello! Welcome to Hour4U.com, I'm Anmol and I'm here to assist you with your event requirements.";
					log.info("Chatgpt response :: "+responseMessage);
					updateConversation(webhookEventRequestDto, historyMessage, null, null);
				}else {
					
					String updatedRawMessage = redisMessagePayload.getRawMessage() + "\n"+"Employer : "+webhookEventRequestDto.getText();
					prompt = PromptTemplates.RAW_EMPLOYMENT_DETAILS_PREFIX+"\n\n"+PromptTemplates.RAW_EMPLOYMENT_CHAT_HISTORY_PREFIX+updatedRawMessage
							+"\n\n"+PromptTemplates.RAW_EMPLOYMENT_GUIDELINES+"\n\n"+PromptTemplates.RAW_EMPLOYMENT_DETAILS_SUFFIX;
					redisMessagePayload.setRawMessage(updatedRawMessage);


					log.debug("prompt :: "+prompt);
					ChatResponse chatGptResponseObj = genAIClientService.getChatGptResponse(prompt);
					String chatGptResponse = chatGptResponseObj.getAiResponse();
					//cleanse the response.
					log.debug("chatGptResponse :: "+chatGptResponse);
					
					String responseJsonString = Utils.extractJsonString(chatGptResponse);
					log.debug("Json String :: "+responseJsonString);

					// retry once if response from chatgpt is empty
					if(null==responseJsonString || responseJsonString.equals("")) {
						Thread.sleep(5000);
						chatGptResponseObj = genAIClientService.getChatGptResponse(prompt);
						chatGptResponse = chatGptResponseObj.getAiResponse();
					}

					employmentInputMessage = Utils.parseJSONString(responseJsonString);

					if(null==employmentInputMessage.getContactNumber() || employmentInputMessage.getContactNumber().equals("")) {
						String contactNumber = webhookEventRequestDto.getWaId();
						if(contactNumber.length()>10){
							if(contactNumber.startsWith("+")) {
								contactNumber = contactNumber.substring(1);
							}
							if(contactNumber.length()==12) {
								contactNumber = contactNumber.substring(2);
							}
						}
						employmentInputMessage.setContactNumber(webhookEventRequestDto.getWaId());
					}

					responseMessage = getMissingParameterResponse(employmentInputMessage);
					if(responseMessage.equals("")) {
						log.info("All mandatory details are avaialble now so closing the conversation.");
						responseMessage = PromptTemplates.CLOSING_MESSAGE;

						String shiftTimings = "";
						for(EmploymentShift employmentShift : employmentInputMessage.getShifts()) {
							if(!shiftTimings.equals("")) {
								shiftTimings = shiftTimings+", ";
							}
							shiftTimings = shiftTimings+employmentShift.getStartTime()+"-"+employmentShift.getEndTime();
						}

						String malesRequired = "";
						String femalesRequired = "";

						for(Map<String, VolunteerDetails> volunteerDetailsMap : employmentInputMessage.getRequiredVolunteers()) {
							VolunteerDetails volunteerDetailsMale = volunteerDetailsMap.get(GeneralConstants.MALE);
							if(null!=volunteerDetailsMale && (null!=volunteerDetailsMale.getCount() && !volunteerDetailsMale.getCount().equals(""))) {
								malesRequired = volunteerDetailsMale.getCount()+", "+"Pay/Shift : "+volunteerDetailsMale.getPayPerShift();
								if(null!=volunteerDetailsMale.getDressCode() && !volunteerDetailsMale.getDressCode().equals("")) {
									malesRequired = malesRequired+", Dress Code : "+volunteerDetailsMale.getDressCode();
								}
								if(null!=volunteerDetailsMale.getTravelAllowance() && !volunteerDetailsMale.getTravelAllowance().equals("")) {
									malesRequired = malesRequired+", Travel Allowance : "+volunteerDetailsMale.getTravelAllowance();
								}
							}


							VolunteerDetails volunteerDetailsFemale = volunteerDetailsMap.get(GeneralConstants.FEMALE);
							if(null!=volunteerDetailsFemale && (null!=volunteerDetailsFemale.getCount() && !volunteerDetailsFemale.getCount().equals(""))) {
								femalesRequired = volunteerDetailsFemale.getCount()+", "+"Pay/Shift : "+volunteerDetailsFemale.getPayPerShift();
								if(null!=volunteerDetailsFemale.getDressCode() && !volunteerDetailsFemale.getDressCode().equals("")) {
									femalesRequired = femalesRequired+", Dress Code : "+volunteerDetailsFemale.getDressCode();
								}
								if(null!=volunteerDetailsFemale.getTravelAllowance() && !volunteerDetailsFemale.getTravelAllowance().equals("")) {
									femalesRequired = femalesRequired+", Travel Allowance : "+volunteerDetailsFemale.getTravelAllowance();
								}
							}
						}

						String finalDetails = "Event Name : "+employmentInputMessage.getEventName()+"\n"
								+"Description : "+employmentInputMessage.getJobDescription()+"\n"
								+"City : "+employmentInputMessage.getCity()+"\n"
								+"Location : "+employmentInputMessage.getLocation()+"\n"
								+"Event Date : "+employmentInputMessage.getEventDate()+"\n"
								+"Start Time : "+shiftTimings;

						if(!malesRequired.equals("")) {
							finalDetails = finalDetails+"\n"+"Male Candidates : "+malesRequired;
						}
						if(!femalesRequired.equals("")) {
							finalDetails = finalDetails+"\n"+"Female Candidates : "+femalesRequired;
						}

						responseMessage = responseMessage+"\n"+finalDetails;
						//NOTE - PUTTTING BELOW STATEMENT TO TEST THE FLOW. LATER ON NEED TO REOMOVE IT.
						//mqProducer.pushCreateJobMessage(employmentInputMessage);


						//redisMessageService.del(webhookEventRequestDto.getConversationId());
						
						deleteConversation(webhookEventRequestDto);
						
						//updateConversation(webhookEventRequestDto, redisMessagePayload.getRawMessage(), redisMessagePayload.getEmploymentInputMessage(), "Expired");
						
						System.out.println("responseMessage :: "+responseMessage);

					}else {

						redisMessagePayload.setRawMessage(redisMessagePayload.getRawMessage()+"\n"+"ChatGpt : "+responseMessage);
						redisMessagePayload.setEmploymentInputMessage(employmentInputMessage);
						updateConversation(webhookEventRequestDto, redisMessagePayload.getRawMessage(), redisMessagePayload.getEmploymentInputMessage(), null);

					}


					/*if(null!=employmentInputMessage.getMissingInformationQuestion() && !employmentInputMessage.getMissingInformationQuestion().equalsIgnoreCase("")) {
						responseMessage = employmentInputMessage.getMissingInformationQuestion();

					}else if(null!=employmentInputMessage.getClosingMessage() && !employmentInputMessage.getClosingMessage().equals("")) {

						boolean foundPayDetails = true;
						for(Map<String, VolunteerDetails> volunteerDetailsMap : employmentInputMessage.getRequiredVolunteers()) {
							VolunteerDetails volunteerDetailsMale = volunteerDetailsMap.get(GeneralConstants.MALE);
							if(null!=volunteerDetailsMale && (null==volunteerDetailsMale.getPayPerShift() || volunteerDetailsMale.getPayPerShift().equals(""))) {
								foundPayDetails = false;
							}

							VolunteerDetails volunteerDetailsFemale = volunteerDetailsMap.get(GeneralConstants.FEMALE);
							if(null!=volunteerDetailsFemale && (null==volunteerDetailsFemale.getPayPerShift() || volunteerDetailsFemale.getPayPerShift().equals(""))) {
								foundPayDetails = false;
							}
						}

						if(null==employmentInputMessage.getJobDescription() || employmentInputMessage.getJobDescription().trim().equals("")){
							log.info("Job description is not provided, so askig the user for job description");
							responseMessage = PromptTemplates.ASK_USER_TYPE_OF_WORK;
						}

						else if(null==employmentInputMessage.getLocation() || employmentInputMessage.getLocation().trim().equals("")){
							log.info("Location is not provided, so askig the user for location details");
							responseMessage = PromptTemplates.ASK_USER_EVENT_LOCATION_PROMPT;
						}

						else if(null==employmentInputMessage.getEventDate() || employmentInputMessage.getEventDate().trim().equals("")){
							log.info("Event date is not provided, so askig the user for event date");
							responseMessage = PromptTemplates.ASK_USER_EVENT_DATE_PROMPT;
						}

						else if(null==employmentInputMessage.getShifts() || employmentInputMessage.getShifts().size()<=0){
							log.info("Shift time is not provided, so askig the user for shift timings");
							responseMessage = PromptTemplates.ASK_USER_EVENT_TIME_PROMPT;
						}

						else if(null==employmentInputMessage.getRequiredVolunteers() || employmentInputMessage.getRequiredVolunteers().size()<=0){
							log.info("Workers details are not provided, so askig the user for worker details");
							responseMessage = PromptTemplates.ASK_USER_REQUIRED_VOLUNTEERS;
						}

						else if(!foundPayDetails) {
							log.info("Shift payment is not provided, so askig the user for shift payment details.");
							responseMessage = PromptTemplates.ASK_USER_BUDGET;
						}else {
							log.info("All mandatory details are avaialble now so closing the conversation.");
							responseMessage = employmentInputMessage.getClosingMessage();

							responseMessage = responseMessage+"\n"+responseJsonString;

							//NOTE - PUTTTING BELOW STATEMENT TO TEST THE FLOW. LATER ON NEED TO REOMOVE IT.
							//mqProducer.pushCreateJobMessage(employmentInputMessage);


							redisMessageService.del(webhookEventRequestDto.getConversationId());
							System.out.println("responseMessage :: "+responseMessage);

						}
					} */
				}
				//send message to whatsapp number
				whatspMsgServiceApiHandler.postSessionMessage(webhookEventRequestDto.getWaId(), "messageText", responseMessage);

			}

		} catch (Exception e) {
			log.error("Failed to process message " + e.getMessage());
			e.printStackTrace();
		}

	}

	public void flushRedisCache() {
		redisMessageService.flushRedisCache();
	}

	private String getMissingParameterResponse(EmploymentInputMessage employmentInputMessage) {

		String responseMessage = "";

		boolean foundPayDetails = false;
		int requiredMales = 0;
		int requiredFemales = 0;
		for(Map<String, VolunteerDetails> volunteerDetailsMap : employmentInputMessage.getRequiredVolunteers()) {
			VolunteerDetails volunteerDetailsMale = volunteerDetailsMap.get(GeneralConstants.MALE);
			if(null!=volunteerDetailsMale && null!=volunteerDetailsMale.getCount() && !volunteerDetailsMale.getCount().equals("")) {
				try {
					requiredMales = Integer.parseInt(volunteerDetailsMale.getCount());
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			if(null!=volunteerDetailsMale && (null!=volunteerDetailsMale.getPayPerShift() && !volunteerDetailsMale.getPayPerShift().equals(""))) {
				foundPayDetails = true;
			}

			VolunteerDetails volunteerDetailsFemale = volunteerDetailsMap.get(GeneralConstants.FEMALE);
			if(null!=volunteerDetailsFemale && (null!=volunteerDetailsFemale.getPayPerShift() && !volunteerDetailsFemale.getPayPerShift().equals(""))) {
				foundPayDetails = true;
			}

			if(null!=volunteerDetailsFemale && null!=volunteerDetailsFemale.getCount() && !volunteerDetailsFemale.getCount().equals("")) {
				try {
					requiredFemales = Integer.parseInt(volunteerDetailsFemale.getCount());
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		boolean foundStartTimingDetails = false;
		boolean foundEndTimingDetails = false;
		for (EmploymentShift employmentShift : employmentInputMessage.getShifts()) {
			if(null!=employmentShift) {
				if(null!=employmentShift.getStartTime() && !employmentShift.getStartTime().equals("")) {
					foundStartTimingDetails = true;
				}
				if(null!=employmentShift.getEndTime() && !employmentShift.getEndTime().equals("")) {
					foundEndTimingDetails = true;
				}
			}
		}

		if(null==employmentInputMessage.getJobDescription() || employmentInputMessage.getJobDescription().trim().equals("")){
			log.info("Job description is not provided, so askig the user for job description");
			responseMessage = PromptTemplates.ASK_USER_TYPE_OF_WORK;
		}

		else if(null==employmentInputMessage.getLocation() || employmentInputMessage.getLocation().trim().equals("")){
			log.info("Location is not provided, so askig the user for location details");
			responseMessage = PromptTemplates.ASK_USER_EVENT_LOCATION_PROMPT;
		}

		else if(null==employmentInputMessage.getEventDate() || employmentInputMessage.getEventDate().trim().equals("")){
			log.info("Event date is not provided, so askig the user for event date");
			responseMessage = PromptTemplates.ASK_USER_EVENT_DATE_PROMPT;
		}

		else if(null==employmentInputMessage.getShifts() || employmentInputMessage.getShifts().size()<=0 || (!foundStartTimingDetails && !foundEndTimingDetails)){
			log.info("Shift time is not provided, so askig the user for shift timings");
			responseMessage = PromptTemplates.ASK_USER_EVENT_TIME_PROMPT;
		}
		else if(!foundStartTimingDetails) {
			log.info("Shift start time is not provided, so askig the user for shift timings");
			responseMessage = PromptTemplates.ASK_USER_EVENT_TIME_PROMPT;
		}
		
		else if(!foundEndTimingDetails) {
			log.info("Shift end time is not provided, so askig the user for shift timings");
			responseMessage = PromptTemplates.ASK_USER_EVENT_ENDTIME_PROMPT;
		}

		else if(requiredMales<=0 && requiredFemales<=0){
			log.info("Workers details are not provided, so askig the user for worker details");
			responseMessage = PromptTemplates.ASK_USER_REQUIRED_VOLUNTEERS;
		}

		else if(!foundPayDetails) {
			log.info("Shift payment is not provided, so askig the user for shift payment details.");
			responseMessage = PromptTemplates.ASK_USER_BUDGET;
		}
		System.out.println("responseMessage :: "+responseMessage);
		return responseMessage;
	}

	private synchronized boolean isDuplicateMessage(UserConversations userConversationHistory, WebhookEventRequestDto webhookEventRequestDto) {
		log.info("Storing the message Id in conversation in Redis store...");
		boolean duplicateMessage = false;
		
		if(null!=userConversationHistory) {
			log.info("Checking for duplicate Messages :: Incoming Message Id :: "+webhookEventRequestDto.getWhatsappMessageId());
			log.info("Checking for duplicate Messages :: Existing Messages :: "+userConversationHistory.getMessageIds());
			if(userConversationHistory.getMessageIds().contains(webhookEventRequestDto.getWhatsappMessageId())) {
				log.info("Received duplicate message...");
				duplicateMessage = true;
			}else {
				log.info("adding the message Id in conversation history");
				userConversationHistory.getMessageIds().add(webhookEventRequestDto.getWhatsappMessageId());
			}
		}
		redisMessageService.storeMessage(webhookEventRequestDto.getWaId(), userConversationHistory);
		return duplicateMessage;

	}
	
	private RedisMessagePayload getRedisMessagePayload(UserConversations userConversationHistory, WebhookEventRequestDto webhookEventRequestDto) {
		
		RedisMessagePayload redisMessagePayload = null;
		
		List<RedisMessagePayload> userConversations = userConversationHistory.getConversations();
		if(null!=userConversations) {
			for(RedisMessagePayload existingRedisMessagePayload : userConversations) {
				if(existingRedisMessagePayload.getConversationId().equals(webhookEventRequestDto.getConversationId()) 
						&& existingRedisMessagePayload.getConversationStatus().equalsIgnoreCase("Active")){
					redisMessagePayload = existingRedisMessagePayload;
				}
			}
		}
		
		if(null==redisMessagePayload) {
			redisMessagePayload = new RedisMessagePayload();
			redisMessagePayload.setConversationId(webhookEventRequestDto.getConversationId());
			redisMessagePayload.setRawMessage("");
			redisMessagePayload.getMessageIds().add(webhookEventRequestDto.getWhatsappMessageId());
			redisMessagePayload.setConversationStatus("Active");
			redisMessagePayload.setLastUpdatedDate(new Date());
			userConversationHistory.getConversations().add(redisMessagePayload);
		}else {
			redisMessagePayload.getMessageIds().add(webhookEventRequestDto.getWhatsappMessageId());
			redisMessagePayload.setLastUpdatedDate(new Date());
		}
		redisMessageService.storeMessage(webhookEventRequestDto.getWaId(), userConversationHistory);
		return redisMessagePayload;
		
	}
	
	private UserConversations getUserConversationHistory(WebhookEventRequestDto webhookEventRequestDto) {
		UserConversations userConversationHistory = redisMessageService.retrieveMessage(webhookEventRequestDto.getWaId());
		if(null==userConversationHistory) {
			userConversationHistory = new UserConversations();
			userConversationHistory.setUserId(webhookEventRequestDto.getWaId());
			redisMessageService.storeMessage(webhookEventRequestDto.getWaId(), userConversationHistory);
		}
		return userConversationHistory;
	}

	private synchronized void updateConversation(WebhookEventRequestDto webhookEventRequestDto, String rawMessage, EmploymentInputMessage inputMessage, String status) {
		log.info("Updating the conversation in Redis store...");
		UserConversations userConversationHistory = redisMessageService.retrieveMessage(webhookEventRequestDto.getWaId());
		
		List<RedisMessagePayload> userConversations = userConversationHistory.getConversations();
		if(null!=userConversations) {
			for(RedisMessagePayload existingRedisMessagePayload : userConversations) {
				if(existingRedisMessagePayload.getConversationId().equals(webhookEventRequestDto.getConversationId()) 
						&& existingRedisMessagePayload.getConversationStatus().equalsIgnoreCase("Active")){
					 existingRedisMessagePayload.setRawMessage(rawMessage);
					 if(null!=inputMessage) {
						 existingRedisMessagePayload.setEmploymentInputMessage(inputMessage);
					 }
					 if(null!=status && !status.equals("")) {
						 existingRedisMessagePayload.setConversationStatus(status);
					 }
				}
			}
		}
		redisMessageService.storeMessage(webhookEventRequestDto.getWaId(), userConversationHistory);

	}
	
	private synchronized void deleteConversation(WebhookEventRequestDto webhookEventRequestDto) {
		log.info("Updating the conversation in Redis store...");
		UserConversations userConversationHistory = redisMessageService.retrieveMessage(webhookEventRequestDto.getWaId());
		
		List<RedisMessagePayload> userConversations = userConversationHistory.getConversations();
		if(null!=userConversations) {
			
			for (int i = 0; i < userConversations.size(); i++) {
			    RedisMessagePayload existingRedisMessagePayload = userConversations.get(i);
			    if(existingRedisMessagePayload.getConversationId().equals(webhookEventRequestDto.getConversationId()) 
						&& existingRedisMessagePayload.getConversationStatus().equalsIgnoreCase("Active")){
					userConversationHistory.getConversations().remove(i);
				}
			    
			}
		}
		redisMessageService.storeMessage(webhookEventRequestDto.getWaId(), userConversationHistory);

	}

}