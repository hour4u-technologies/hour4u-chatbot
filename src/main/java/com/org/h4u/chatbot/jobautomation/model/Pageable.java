package com.org.h4u.chatbot.jobautomation.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Pageable {
	
	private Sort sort;
    private int offset;
    private int pageNumber;
    private int pageSize;
    private boolean paged;
    private boolean unpaged;

}
