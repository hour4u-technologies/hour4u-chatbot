package com.org.h4u.chatbot.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Chatgpt exception.
 */
@Getter
@Setter
public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = 1243942154053999741L;
	
	private String message;
	private HttpStatus httpStatus;
	
	/**
     * Instantiates a new Chatgpt exception.
     *
     * @param message the message
     */
	public InvalidRequestException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
}
