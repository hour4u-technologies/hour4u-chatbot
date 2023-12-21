package com.org.h4u.chatbot.jobautomation;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.h4u.chatbot.common.GeneralConstants;
import com.org.h4u.chatbot.domain.EmploymentInputMessage;
import com.org.h4u.chatbot.domain.VolunteerDetails;
import com.org.h4u.chatbot.jobautomation.model.Employer;
import com.org.h4u.chatbot.jobautomation.model.JobRequestPayload;
import com.org.h4u.chatbot.jobautomation.model.JobType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateJobProcessorService {
	
	@Autowired
	Hour4UJobService hour4uJobService;
	
	public void processCreateJobMessage(EmploymentInputMessage employmentInputMessage) {
		
		log.info("Started processing the incoming create job message...");
		
		// get the employer details based on contact number
		Employer employer = hour4uJobService.getEmployer(employmentInputMessage.getContactNumber());
		
		//get the job type details based on description
		JobType jobType = hour4uJobService.getJobTypes(employmentInputMessage.getJobDescription());
		
		// create Job object to create at backend
		
		if(null!=jobType && null!=employer){
			
			JobRequestPayload jobRequestPayload = new JobRequestPayload();
			jobRequestPayload.setJobTypeId(jobType.getId());
			jobRequestPayload.setEmployerName(employer.getName());
			jobRequestPayload.setEmployerId(employer.getId());
			jobRequestPayload.setDescription(employmentInputMessage.getJobDescription());
			
			List<Map<String, VolunteerDetails>> volunteers = employmentInputMessage.getRequiredVolunteers();
			
			if(null!=volunteers && volunteers.size()>0) {
				for(Map<String, VolunteerDetails> volunteer : volunteers) {
					if(null!=volunteer.get(GeneralConstants.MALE)) {
						VolunteerDetails volunteerDetail = volunteer.get(GeneralConstants.MALE);
						jobRequestPayload.setDressCodeMale(volunteerDetail.getDressCode());
					}else if(null!=volunteer.get(GeneralConstants.FEMALE)) {
						VolunteerDetails volunteerDetail = volunteer.get(GeneralConstants.FEMALE);
						jobRequestPayload.setDressCodeFemale(volunteerDetail.getDressCode());
					}
				}
			}
			if(null!=employmentInputMessage.getPrerequisites() && employmentInputMessage.getPrerequisites().size()>0) {
				String delimiter = " ";
				jobRequestPayload.setPrerequisites(String.join(delimiter, employmentInputMessage.getPrerequisites()));
			}
			
			jobRequestPayload.setTitle(jobType.getName()+" - "+employer.getName());
				
		}
		
		
		
		
	}

}
