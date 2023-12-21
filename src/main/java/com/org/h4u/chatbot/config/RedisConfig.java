package com.org.h4u.chatbot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class RedisConfig {

	@Value("${common.redis.serviceurl}")
	private String serviceUrl;

	@Value("${common.redis.pool-config.maxIdle:64}")
	private Integer maxIdle;

	@Value("${common.redis.pool-config.maxTotal:64}")
	private Integer maxTotal;

	@Value("${common.redis.pool-config.minIdle:8}")
	private Integer minIdle;

	
	@Bean
	public JedisPool jedisPool() {

		final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setMinIdle(minIdle);
		log.info("JedisPoolConfig Initialize ........");
		final JedisPool jedisPool = new JedisPool(jedisPoolConfig, serviceUrl);
		//new JedisPool(jedisPoolConfig, host, port, timeout, password);
		log.info("JedisPool Initialize ........");
		return jedisPool;
	}

}

