package com.org.h4u.chatbot.message.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.message.MqProducer;

@Service
@Slf4j
public class GenAIJobMessageProducerService {

    @Autowired
    private MqProducer mqProducer;
    
    @Async
    public void sendJobAutomationMessage(EmploymentInputMessage employmentInputMessage) {
        try {
            mqProducer.pushCreateJobMessage(employmentInputMessage);
        } catch (Exception e) {
            log.error("Error while sending work log notification", e);
        }
    }
   
}
