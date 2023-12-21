package com.org.h4u.chatbot;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = "com.org.h4u")
@EnableConfigurationProperties
public class App {
    public static void main(String[] args) {
    	ApplicationContext applicationContext =  SpringApplication.run(App.class, args);
        Arrays.stream(applicationContext.getBeanDefinitionNames());
    }

}