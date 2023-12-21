package com.org.h4u.chatbot.message.consumer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.jobautomation.CreateJobProcessorService;

/**
 * The type Employment action consumer.
 */
@Service("employmentActionConsumer")
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@RabbitListener(queues = "Q_GENAI_JOB_AUTOMATION")
public class GenAIJobAutomationConsumer {

	private final CreateJobProcessorService createJobProcessorService;

    /**
     * <p>
     * create job based on input provided the employer on whatsapp
     * </p>
     *
     * @param request -- employment average rating request
     * @throws Exception the exception
     */
    @RabbitHandler
	public void createJob(EmploymentInputMessage request) throws Exception {
		log.info("GenAIJobAutomationConsumer >> createJob");
		createJobProcessorService.processCreateJobMessage(request);
		log.info("createJob Listener Ended");
	}

}
