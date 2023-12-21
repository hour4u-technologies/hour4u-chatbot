package com.org.h4u.chatbot.jobautomation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The type Employer.
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employer {

	private String id;
	
	private String name;

    private String address;
    
    private String contactNo;

    private String gstNumber;
    
    private String email;

    private String profilePic;

    private Double averageRating;

    //Default advance payment value is 30
    private Integer advanceAmountPercentage=20;

    private String employerDid;
    private String contractDocKey;
    private String hyperSignDocId;
    private String  panCardS3Key;
    private String gstDocumentS3Key;
    private String signatureS3Key;

}

