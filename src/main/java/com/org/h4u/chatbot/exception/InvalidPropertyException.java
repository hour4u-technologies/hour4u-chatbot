package com.org.h4u.chatbot.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

/**
 * The type Invalid property exception.
 */

@Getter
@Setter
public class InvalidPropertyException extends RuntimeException {

	private static final long serialVersionUID = -773136201293757165L;
	
	private String message;
	private HttpStatus httpStatus;
	
	/**
     * Instantiates a new Invalid property exception.
     *
     * @param message the message
     * @param httpStatus the httpStatus
     */
	public InvalidPropertyException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
}
