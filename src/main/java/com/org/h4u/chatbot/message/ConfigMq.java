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

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

/**
 * The type Config mq.
 */
@Configuration
@EnableRabbit
public class ConfigMq {

    /**
     * Rabbit listener container factory simple rabbit listener container factory.
     *
     * @param connectionFactory the connection factory
     * @return the simple rabbit listener container factory
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //factory.setConcurrentConsumers(3);
        //factory.setMaxConcurrentConsumers(10);
        factory.setDefaultRequeueRejected(false);
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        messageConverter.getJavaTypeMapper().addTrustedPackages("*");        
        factory.setMessageConverter(messageConverter);
        factory.setErrorHandler(errorHandler());
        //factory.setContainerCustomizer(container -> /* customize the container */);
        return factory;
    }

    /**
     * Error handler error handler.
     *
     * @return the error handler
     */
    @Bean
    public ErrorHandler errorHandler() {
        return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
    }

    /**
     * The type My fatal exception strategy.
     */
    public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

        @Override
        public boolean isFatal(Throwable t) {
            if (t instanceof ListenerExecutionFailedException) {
                ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
                logger.error("Failed to process inbound message from queue "
                        + lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
                        + "; failed message: " + lefe.getFailedMessage(), t);
            }
            return super.isFatal(t);
        }

    }

    /**
     * Q job seeker action queue.
     *
     * @return the queue
     */
    @Bean
    public Queue qJobSeekerAction() {
        return new Queue("Q_GENAI_JOB_AUTOMATION", false);
    }

}
