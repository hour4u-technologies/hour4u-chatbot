package com.org.h4u.chatbot.jobautomation.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobTypeResponse {
	
	private List<JobType> content;
    private Pageable pageable;
    private int totalElements;
    private int totalPages;
    private boolean last;
    private int size;
    private int number;
    private Sort sort;
    private int numberOfElements;
    private boolean first;

}
