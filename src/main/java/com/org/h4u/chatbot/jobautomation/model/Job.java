package com.org.h4u.chatbot.jobautomation.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Job {
	
private String employerId;

	private String id;
    
    private String employerName;

    private String jobTypeId;

    private String code;

    private String title;

    private List<String> tasks;

    private String description;

    private String prerequisites;

    private String dressCodeMale;

    private String dressCodeFemale;

    private String requirements;

    private boolean approved;

    private boolean globalTemplate;

}
