package com.org.h4u.chatbot.jobautomation.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JobType {
	
	private String id;
	
	private String name;

    private String notes;

    private String jobCategoryId;

    private String jobCategoryName;

    private List<JobTypePrice> jobSeekerPrices;

    private List<JobTypePrice> employerPrices;

    private String createdBy;

    private String updatedBy;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdOn;

     @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedOn;

    private Boolean isDelete;

}
