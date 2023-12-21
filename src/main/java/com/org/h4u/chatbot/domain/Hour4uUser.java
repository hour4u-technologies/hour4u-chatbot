package com.org.h4u.chatbot.domain;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Hour4uUser {
    private Group accountType;
    private String accountId;

    private String jobSeekerId;
    private List<String> authorities;

    private String employerId;
    private String userName;

}
