/*
 *
 *  *
 *  *  Copyright 2020  Codemiro Technologies Pvt Ltd
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and  limitations under the License.
 *  *
 *
 */

package com.org.h4u.chatbot.message;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import com.org.h4u.chatbot.domain.EmploymentInputMessage;

/**
 * The type Mq config.
 */
@Configuration
public class MqConfig {

	/**
	 * The Mq properties.
	 */
	@Autowired
	MqProperties mqProperties;

	private static final boolean NON_DURABLE = false;
	/**
	 * The constant TOPIC_EXCHANGE_NAME.
	 */
	public final static String TOPIC_EXCHANGE_NAME = "action.exchange";

	public final static String Q_GENAI_JOB_AUTOMATION = "Q_GENAI_JOB_AUTOMATION";
	
	public final static String TOPIC_JOB_ACTION_KEY_NAME = "*.job.action";

	/**
	 * Topic bindings declarables.
	 *
	 * @return the declarables
	 */
	@Bean
	public Declarables topicBindings() {
		Queue topicQueue1 = new Queue(Q_GENAI_JOB_AUTOMATION, NON_DURABLE);
		TopicExchange topicExchange = new TopicExchange(TOPIC_EXCHANGE_NAME);
		return new Declarables(
				topicQueue1,
				topicExchange,
				BindingBuilder
				.bind(topicQueue1)
				.to(topicExchange).with(TOPIC_JOB_ACTION_KEY_NAME)
				);
	}

	/**
	 * Consumer jackson 2 message converter mapping jackson 2 message converter.
	 *
	 * @return the mapping jackson 2 message converter
	 */
	@Bean
	public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
		return new MappingJackson2MessageConverter();
	}

	/**
	 * Rabbit template rabbit template.
	 *
	 * @param connectionFactory the connection factory
	 * @return the rabbit template
	 */
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter());
		return rabbitTemplate;
	}

	/**
	 * Message converter jackson 2 json message converter.
	 *
	 * @return the jackson 2 json message converter
	 */
	@Bean
	public Jackson2JsonMessageConverter messageConverter() {
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		DefaultJackson2JavaTypeMapper classMapper = new DefaultJackson2JavaTypeMapper();

		Map<String, Class<?>> idClassMapping = new HashMap<>();
		idClassMapping.put(
				"com.org.h4u.model.EmploymentInputMessage", EmploymentInputMessage.class);
		classMapper.setIdClassMapping(idClassMapping);
		converter.setClassMapper(classMapper);

		return converter;
	}

}
