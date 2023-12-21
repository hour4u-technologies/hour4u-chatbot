package com.org.h4u.chatbot.jobautomation.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter @Setter
public class AuthTokenResponse {
	
	private String access_token;
    private String token_type;
    private String refresh_token;
    private Long expires_in;
    private String scope;
    private String jti;
    private String accountName;
    private String email;
    private String accountId;
    private List<String> roles;
    private Long tokenExpirationTime;
}
