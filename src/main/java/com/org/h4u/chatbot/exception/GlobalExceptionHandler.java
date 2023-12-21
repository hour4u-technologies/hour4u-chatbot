package com.org.h4u.chatbot.exception;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.org.h4u.chatbot.common.GeneralConstants;

/**
 * Global exception handler class
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
     * Handle http media type not supported exception.
     *
     * @param ex the HttpMediaTypeNotSupportedException object
     * @param headers the HttpHeaders
     * @param status the HttpStatusCode
     * @param request the WebRequest
     * 
     * @return ResponseEntity with ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		
		List<String> details = new ArrayList<String>();
		
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, builder.toString()));

        ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.BAD_REQUEST, GeneralConstants.INVALID_JSON ,details);
		
		return ResponseEntityBuilder.build(err);
	
	}
    
    /**
     * Handle handleHttpMessageNotReadable exception.
     *
     * @param ex the handleHttpMessageNotReadable object
     * @param headers the HttpHeaders
     * @param status the HttpStatusCode
     * @param request the WebRequest
     * 
     * @return ResponseEntity with ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
		List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
        
        ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST, GeneralConstants.MALFORMED_JSON_REQUEST ,details);
		
		return ResponseEntityBuilder.build(err);
    }
    
    /**
     * Handle Exception exception.
     *
     * @param ex Exception object
     * 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        logger.error(ex.getMessage());
        
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST, GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
		
    }

    /**
     * Handle NullPointerException exception.
     *
     * @param ex the NullPointerException object
     * 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST, GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
    }
    
    /**
     * Handle IllegalAccessException exception.
     *
     * @param ex the IllegalAccessException object
     * 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<Object> handleIllegalAccessException(IllegalAccessException ex) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.UNAUTHORIZED, GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
    }

    /**
     * Handle ChatgptException exception.
     *
     * @param ex the ChatgptException object 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(ChatgptException.class)
    public ResponseEntity<Object> handleGptMessageException(ChatgptException ex) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),ex.getHttpStatus(), GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
    }
    
    /**
     * Handle InvalidRequestException exception.
     *
     * @param ex the InvalidRequestException object 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(InvalidRequestException ex) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),HttpStatus.BAD_REQUEST, GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
    }
    
    
    
    
    /**
     * Handle InvalidPropertyException exception.
     *
     * @param ex the InvalidPropertyException object
     * 
     * @return ResponseEntity with ApiError object
     */
    @ExceptionHandler(InvalidPropertyException.class)
    public ResponseEntity<Object> handleInvalidPropertyException(InvalidPropertyException ex) {
        logger.error(ex.getMessage());
        List<String> details = new ArrayList<String>();
        details.add(String.format(GeneralConstants.ERROR_RESPONSE_FORMAT, ex.getLocalizedMessage()));
		
		ApiError err = new ApiError(LocalDateTime.now(),ex.getHttpStatus(), GeneralConstants.ERROR_MESSAGE ,details);
		
		return ResponseEntityBuilder.build(err);
    }

}
