package com.codemiro.hour4u.jobservice.business;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;

import com.codemiro.hour4u.jobservice.domain.Employment;
import com.codemiro.hour4u.jobservice.domain.EmploymentAttendance;
import com.codemiro.hour4u.jobservice.domain.JobApplication;
import com.codemiro.hour4u.jobservice.domain.JobType;
import com.codemiro.hour4u.jobservice.domain.JobTypePrice;
import com.codemiro.hour4u.jobservice.domain.QEmploymentAttendance;
import com.codemiro.hour4u.jobservice.domain.QJobApplication;
import com.codemiro.hour4u.jobservice.repository.EmploymentAttendanceRepository;
import com.codemiro.hour4u.jobservice.repository.JobApplicationRepository;
import com.codemiro.hour4u.jobservice.util.enums.Gender;
import com.codemiro.hour4u.jobservice.util.enums.JobApplicationStatus;
import com.codemiro.hour4u.jobservice.util.enums.Status;
import com.codemiro.hour4u.jobservice.web.response.MyJobResponse;
import com.querydsl.core.BooleanBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The type My job service.
 */
@Slf4j
@Service("MyJobService")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MyJobService {
	private final JobApplicationRepository jobApplicationrepository;
	private final JobApplicationService jobApplicationService;
	private final JobApplicationRepository jobApplicationRepository;
	private final EmploymentService employmentService;
	private final JobService jobService;
	private final JobTypeService jobTypeService;
	private final EmploymentAttendanceService employmentAttendanceService;
	private final EmploymentAttendanceRepository employmentAttendanceRepository;

	/**
	 * Gets my jobs.
	 *
	 * @param jobSeekerId the job seeker id
	 * @param status      the status
	 * @param pageable    the pageable
	 * @return the my jobs
	 */
	@Transactional(readOnly = true)
	public Page<MyJobResponse> getMyJobs(final String jobSeekerId, final String status, Pageable pageable) {
		log.info("looking for jobSeekerId " + jobSeekerId);
		var qJobApplication = QJobApplication.jobApplication;
		var booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(qJobApplication.jobSeekerId.eq(jobSeekerId));
		//boolean isSearchStatusCompleted = false;

		if (status != null) {
			// isSearchStatusCompleted = status.equals(JobApplicationStatus.COMPLETED.name());
			booleanBuilder.and(qJobApplication.status.eq(JobApplicationStatus.valueOf(status)));
		}

		//boolean finalIsSearchStatusCompleted = isSearchStatusCompleted;
		Long pgCount = jobApplicationRepository.count(booleanBuilder.getValue());

		Long tf1 = System.nanoTime();
		//orig
		Pageable pageSorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by(Sort.Direction.DESC, "createdOn"));
		Set<MyJobResponse> myJobResponseSet = jobApplicationRepository
				.findAll(booleanBuilder.getValue(), pageSorted).stream()
				.map(jobApplication -> transform(jobApplication, false, pageable))
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());

		Long tf2 = System.nanoTime();
		//log.info("46.2 AD tf1 time taken in ms " + (tf2-tf1)/1000000);

		List<MyJobResponse> myJobResponseList = new ArrayList<>();
		myJobResponseList.addAll(myJobResponseSet);

		Collections.sort(myJobResponseList, (o1, o2) -> o2.getCreatedOn().compareTo(o1.getCreatedOn()));

		final Page<MyJobResponse> myJobResponsePage = new PageImpl<>(myJobResponseList, pageable, pgCount.longValue());

		log.info("returning from getMyJobs");
		return myJobResponsePage;
	}

	/**
	 * Gets active job.
	 *
	 * @param jobSeekerId the job seeker id
	 * @param pageable    the pageable
	 * @return the active job
	 */
	public MyJobResponse getActiveJob(final String jobSeekerId, final Pageable pageable) {
			var response = new AtomicReference<MyJobResponse>();

			// find active employments
			var employmentParams = new LinkedMultiValueMap<String, String>();
			employmentParams.add("status", Status.Active.name());
			employmentParams.add("inProgress", "true");
			List<Employment> employmentList = employmentService.getAll(employmentParams);
			log.info("employmentList size " + employmentList.size());
			//for(Employment employment : employmentList)
				//log.info("employmentId in progress " + employment.getId());
			
			boolean hasActiveJob = false;
			for (Employment employment : employmentList) {
				var params = new LinkedMultiValueMap<String, String>();
				params.add("status", JobApplicationStatus.APPROVED.toString());
				params.add("jobSeekerId", jobSeekerId);
				//employmentList.forEach(employment -> {
				params.add("employmentId", employment.getId());
				//});
				Page<JobApplication> jobApplications = jobApplicationService.getAll(params, pageable);
				log.info("jobApplications size " + jobApplications.getContent().size());
				

	            if (jobApplications.getContent().size() > 0) {
	                JobApplication jobApplication = jobApplications.getContent().get(0);
	                response.set(transform(jobApplication, true, pageable));
					hasActiveJob = true;
	                break;
	            }
	        }
			
			if(!hasActiveJob) {
				
				// check if any checkout button is pending
				Employment completedEmployment = null;
				log.info("Fetching the the employments which are not checked out yet...");
				List<EmploymentAttendance> employmentAttendances = employmentAttendanceRepository.findByJobSeekerIdAndCheckInIsNotNullAndCheckOutIsNullOrderByCheckInDesc(jobSeekerId);
				log.info("Total unchecked out results :: "+employmentAttendances.size());
				//List<String> testUncheckoutList = new ArrayList<>();
				for (EmploymentAttendance employmentAttendance : employmentAttendances) {
					Employment tempEmployment = employmentService.getById(employmentAttendance.getEmploymentId());
					if(null!= tempEmployment.getStatus() && tempEmployment.getStatus().name().equals(Status.Completed.name()) 
						&& !tempEmployment.isInProgress()) {
						completedEmployment = tempEmployment;
						break;
					}
				}
				
				if(null!=completedEmployment) {
					var params = new LinkedMultiValueMap<String, String>();
					params.add("status", JobApplicationStatus.APPROVED.toString());
					params.add("jobSeekerId", jobSeekerId);
					params.add("employmentId", completedEmployment.getId());
					Page<JobApplication> jobApplications = jobApplicationService.getAll(params, pageable);
					log.info("Unchecked out jobApplications size " + jobApplications.getContent().size());
					if (jobApplications.getContent().size() > 0) {
						JobApplication jobApplication = jobApplications.getContent().get(0);
					    response.set(transform(jobApplication, true, pageable));
					          
					}
				}
			}
						
	        return response.get();
	    }
	
	/**
	 * Gets active job.
	 *
	 * @param jobSeekerId the job seeker id
	 * @param pageable    the pageable
	 * @return the active job
	 */
	public MyJobResponse getActiveJobByEmploymentId(final String jobSeekerId, final String employmentId, final Pageable pageable) {
		var response = new AtomicReference<MyJobResponse>();
		// find active employments
		log.info("Fetching the active employments...");
		if(null!=jobSeekerId && !jobSeekerId.equals("") && null!=employmentId && !employmentId.equals("")) {
			List<String> employmentIds = new ArrayList<>();
			employmentIds.add(employmentId);
			List<String> jobApplicationStatuses = Arrays.asList(JobApplicationStatus.APPROVED.toString(), JobApplicationStatus.COMPLETED.toString());
			log.info("Fetching the active jobs now...");
			List<JobApplication> jobApplications = jobApplicationrepository.findByStatusInAndJobSeekerIdAndEmploymentIdIn(jobApplicationStatuses, jobSeekerId, employmentIds);
			for(JobApplication jobApplication : jobApplications) {
				MyJobResponse mjr = transform(jobApplication, true, pageable);
				if(null!=mjr) {
					response.set(mjr);
				}
				
			}
			log.info("Returning the response...");
		}
		return response.get();
	}
	
	/**
	 * Gets active job.
	 *
	 * @param jobSeekerId the job seeker id
	 * @param pageable    the pageable
	 * @return the active job
	 */
	public List<MyJobResponse> getActiveJobList(final String jobSeekerId, final Pageable pageable) {
		
		List<MyJobResponse> response = new ArrayList<>();
		
		log.info("Processing the getActiveJob");
		// find active employments
		log.info("Fetching the active employments...");
		if(null!=jobSeekerId && !jobSeekerId.equals("")) {
			var employmentParams = new LinkedMultiValueMap<String, String>();
			employmentParams.add("status", Status.Active.name());
			employmentParams.add("inProgress", "true");
			List<Employment> employmentList = employmentService.getAll(employmentParams);
			log.info("employmentList size " + employmentList.size());
			List<String> employmentIds = new ArrayList<>();
			for (Employment employment : employmentList) {
				employmentIds.add(employment.getId());		
			}

			// Now check if there is any employment which is completed but jobSeeker has not checkout yet. It should display in active jobs to remove dependency on local cache.
			log.info("Fetching the the employments which are not checked out yet...");
			List<EmploymentAttendance> employmentAttendances = employmentAttendanceRepository.findByJobSeekerIdAndCheckInIsNotNullAndCheckOutIsNullOrderByCheckInDesc(jobSeekerId);
			log.info("Total unchecked out results :: "+employmentAttendances.size());
			//List<String> testUncheckoutList = new ArrayList<>();
			for (EmploymentAttendance employmentAttendance : employmentAttendances) {
				employmentIds.add(employmentAttendance.getEmploymentId());
				//testUncheckoutList.add(employmentAttendance.getEmploymentId());
			}
			//System.out.println("unchecked out results : "+testUncheckoutList);
			// Now check if there is any employment which is completed but jobSeeker has not submitted the bills yet. It should display in active jobs to remove dependency on local cache.
			List<String> jobApplicationStatuses = Arrays.asList(JobApplicationStatus.APPROVED.toString(), JobApplicationStatus.COMPLETED.toString());
			List<JobApplication> billsUnsubmittedJobApps = jobApplicationrepository.findByBillsSubmittedIsFalseAndStatusInAndJobSeekerId(jobApplicationStatuses, jobSeekerId); 
			log.info("Total bills not submitted  results :: "+billsUnsubmittedJobApps.size());
			//List<String> testUnSubmitList = new ArrayList<>();
			for (JobApplication billsUnsubmittedJobApp : billsUnsubmittedJobApps) {
				employmentIds.add(billsUnsubmittedJobApp.getEmploymentId());
				//testUnSubmitList.add(billsUnsubmittedJobApp.getEmploymentId());
			}
			//System.out.println("unsubmitted out results : "+testUnSubmitList);

			log.info("Fetching the active jobs now...");
			List<JobApplication> jobApplications = jobApplicationrepository.findByStatusInAndJobSeekerIdAndEmploymentIdIn(jobApplicationStatuses, jobSeekerId, employmentIds);
			for(JobApplication jobApplication : jobApplications) {
				MyJobResponse mjr = transform(jobApplication, true, pageable);
				if(null!=mjr) {
					response.add(mjr);			}
				
			}
			log.info("Returning the response...");
		}
		return response;
	}

	/**
	 * Transform my job response.
	 *
	 * @param jobApplication the job application
	 * @return the my job response
	 */
	@Transactional(readOnly = true)
	MyJobResponse transform(final JobApplication jobApplication, boolean forActiveJob, Pageable pageable
	    ) {
	        Long startTime = System.nanoTime();
	        final var jobInfo = jobService.getById(jobApplication.getJobId());
	        final var jobTypeInfo = jobTypeService.getById(jobInfo.getJobTypeId());
	        final var employment = employmentService.getById(jobApplication.getEmploymentId());
	        if (employment == null) {
	            return null;
	        }
	        BigDecimal totalHours = BigDecimal.ZERO;
	        var params = new LinkedMultiValueMap<String, String>();
	        params.add("employmentId", jobApplication.getEmploymentId());
	        params.add("jobSeekerId", jobApplication.getJobSeekerId());
	        Page<EmploymentAttendance> attendances = employmentAttendanceService.getAll(params, pageable);
			
			if((employment.getStatus().name()).equals(Status.Completed.name()) && attendances.getContent().size()<=0) {
				return null;
			}
			
			String checkinTime = "";
			String checkoutTime = "";
	        for (EmploymentAttendance attendance : attendances.getContent()) {
	            if (attendance.getTotalRecordedTime() != null) {
	                totalHours = BigDecimal.valueOf(attendance.getTotalRecordedTime());
	            }
				if(null!=attendance.getCheckIn()){
					checkinTime = attendance.getCheckIn().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
				}
				if(null!=attendance.getCheckOut()) {
					checkoutTime = attendance.getCheckOut().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
				}
	        }
	        MyJobResponse mjr = MyJobResponse.builder()
	                .title(jobInfo.getTitle())
	                .jobId(jobApplication.getJobId())
	                .location(employment.getLocation())
	                .employmentType(employment.getEmploymentType())
	                .recurringDays(employment.getRecurringDays())
	                .dates(employment.getDates())
	                .employmentId(employment.getId())
	                .employmentTitle(employment.getEmploymentTitle())
	                .employerId(employment.getEmployerId())
	                .status(jobApplication.getStatus())
	                .basePrice(jobTypeInfo != null ? jobTypeInfo.getJobSeekerPrices().stream()
	                        .map(JobTypePrice::getBasePrice).collect(Collectors.toUnmodifiableList()) : null)
	                .hourlyRate(jobApplication.getHourlyRate())
	                .totalHours(totalHours)
	                .attendanceAtVenueRequired(employment.isAttendanceAtVenueRequired())
	                .attendanceLocationTrackerRequired(employment.isAttendanceLocationTrackerRequired())
	                .attendanceLogInSelfieRequired(employment.isAttendanceLogInSelfieRequired())
	                .attendanceLogOutSelfieRequired(employment.isAttendanceLogOutSelfieRequired())
	                .jobTypeId(jobTypeInfo.getId())
	                .jobTypeName(jobTypeInfo.getName())
	                .createdOn(jobApplication.getCreatedOn())
					.billsSubmitted(jobApplication.isBillsSubmitted())
					.checkInTime(checkinTime)
					.checkOutTime(checkoutTime)
	                .build();
	        if (Objects.nonNull(employment.getJobSeekerPaymentInfo())
	                && Objects.nonNull(employment.getJobSeekerPaymentInfo_female())
	        ) {
	            mjr.setBasePrice(Arrays.asList(employment.getJobSeekerPaymentInfo().getBasePrice()
	                    , employment.getJobSeekerPaymentInfo_female().getBasePrice()
	            ));
	        }
	        BigDecimal myBasePrice = calculateBasePrice(jobTypeInfo, jobApplication, employment);
	        mjr.setMyBasePrice(myBasePrice);
	        return mjr;
	    }

	private BigDecimal calculateBasePrice(JobType jobTypeInfo, JobApplication jobApplication, Employment employment) {
		// fetch base price from employment
		if (jobApplication.getGender() == Gender.Male
				&& Objects.nonNull(employment.getJobSeekerPaymentInfo())
				) {
			return employment.getJobSeekerPaymentInfo().getBasePrice();
		}

		if (jobApplication.getGender() == Gender.Female
				&& Objects.nonNull(employment.getJobSeekerPaymentInfo_female())
				) {
			return employment.getJobSeekerPaymentInfo_female().getBasePrice();
		}
		//If base price not present in employment fetch It from jobType
		return jobTypeInfo.getJobSeekerPrices().stream().filter(jt -> jt.getGender() == jobApplication.getGender()).map(JobTypePrice::getBasePrice).findAny().orElse(BigDecimal.ZERO);
	}

	@Transactional(readOnly = true)
	public List<MyJobResponse> getMyJobsList(final String jobSeekerId, Pageable pageable) {
		//log.info("getMyJobsList looking for jobSeekerId " + jobSeekerId);
		var qJobApplication = QJobApplication.jobApplication;
		var booleanBuilder = new BooleanBuilder();
		booleanBuilder.and(qJobApplication.jobSeekerId.eq(jobSeekerId));
		Page<JobApplication> jobApplications = jobApplicationRepository.findAll(booleanBuilder.getValue(), PageRequest.of(0, Integer.MAX_VALUE));
		List<JobApplication> jobApplicationList = jobApplications.getContent();

		//Long pgCount = jobApplicationRepository.count(booleanBuilder.getValue());
		//log.info("getMyJobsList jobList " + pgCount);

		var qEmploymentAttendance = QEmploymentAttendance.employmentAttendance;
		var booleanBuilderAtt = new BooleanBuilder();
		booleanBuilderAtt.and(qEmploymentAttendance.jobSeekerId.eq(jobSeekerId));
		booleanBuilderAtt.and(qEmploymentAttendance.totalRecordedTime.isNotNull());
		Page<EmploymentAttendance> attendances = employmentAttendanceRepository.findAll(booleanBuilderAtt.getValue(), PageRequest.of(0, Integer.MAX_VALUE));

		//pgCount = employmentAttendanceRepository.count(booleanBuilderAtt.getValue());
		//log.info("getMyJobsList attList " + pgCount);

		List<String> removalList = new ArrayList<String>();
		for (JobApplication jobApplication : jobApplicationList) {
			var attendanceOptional = attendances.stream()
					.filter(att -> att.getEmploymentId().equalsIgnoreCase(jobApplication.getEmploymentId())
							&& att.getTotalRecordedTime() > 0).findFirst();
			if (attendanceOptional.isPresent()) {
			} else removalList.add(jobApplication.getId());
		}

		Set<MyJobResponse> myJobResponseSet = jobApplicationList.stream()
				.filter(jobApplication -> !removalList.contains(jobApplication.getId()))
				.sorted(Comparator.comparing(JobApplication::getCreatedOn).reversed())
				.limit(pageable.getPageSize())
				.map(jobApplication -> transformForList(jobApplication))
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableSet());

		List<MyJobResponse> myJobResponseList = new ArrayList<MyJobResponse>();
		myJobResponseList.addAll(myJobResponseSet);

		//log.info("returning from getMyJobsList");
		return myJobResponseList;
	}

	@Transactional(readOnly = true)
	MyJobResponse transformForList(final JobApplication jobApplication) {
		//Long tc1 = System.nanoTime();
		final var jobInfo = jobService.getById(jobApplication.getJobId());
		final var employment = employmentService.getById(jobApplication.getEmploymentId());
		/*Long tc2 = System.nanoTime();
		log.info("383 AD tc1 time taken in ms " + (tc2-tc1)/1000000);*/
		if (employment == null) {
			return null;
		}

		//log.info("getting employment " + employment.getEmploymentTitle());

		MyJobResponse mjr = MyJobResponse.builder()
				.title(jobInfo.getTitle())
				.jobId(jobApplication.getJobId())
				.employmentType(employment.getEmploymentType())
				.dates(employment.getDates())
				.employmentId(employment.getId())
				.employmentTitle(employment.getEmploymentTitle())
				.employerId(employment.getEmployerId())
				.build();

		return mjr;
	}
}

