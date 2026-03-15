package com.example.crm.service.impl;

import com.example.crm.dto.request.ApplyForJobRequest;
import com.example.crm.dto.response.ApplicationResponse;
import com.example.crm.dto.response.CandidateResponse;
import com.example.crm.entity.Application;
import com.example.crm.entity.Candidate;
import com.example.crm.entity.Job;
import com.example.crm.entity.User;
import com.example.crm.exception.ResourceNotFoundException;
import com.example.crm.exception.UnauthorizedAccessException;
import com.example.crm.mapper.ApplicationMapper;
import com.example.crm.mapper.CandidateMapper;
import com.example.crm.repository.ApplicationRepository;
import com.example.crm.repository.CandidateRepository;
import com.example.crm.repository.JobRepository;
import com.example.crm.repository.UserRepository;
import com.example.crm.service.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationMapper applicationMapper;
    @Mock
    private CandidateMapper candidateMapper;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private ApplicationResponse applicationResponse;
    @InjectMocks
    private ApplicationServiceImpl applicationService;



    @Test
    public void getJobApplicationsNoJobFoundTest() {

        Long jobId = 1L;
        when(jobRepository.existsById(jobId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> applicationService.getJobApplications(jobId));

    }


    @Test
    public void getJobApplicationsFineTest() {

        applicationResponse.setId(1L);
        Application res1  = Application.builder().id(1L).build();
        Application res2  = Application.builder().id(2L).build();
        ApplicationResponse resp1  = ApplicationResponse.builder().id(1L).build();
        ApplicationResponse resp2  = ApplicationResponse.builder().id(2L).build();
        List<Application> res= new ArrayList<>();
        List<ApplicationResponse> resp = new ArrayList<>();
        resp.add(resp1);
        resp.add(resp2);
        res.add(res1);
        res.add(res2);


        Long jobId = 1L;
        when(jobRepository.existsById(jobId)).thenReturn(true);
        when(applicationRepository.findAllByJobIdAndIsActiveTrue(jobId)).thenReturn(res);


        when(applicationMapper.toResponse(res1)).thenReturn(resp1);
        when(applicationMapper.toResponse(res2)).thenReturn(resp2);
        List<ApplicationResponse> result = applicationService.getJobApplications(jobId);

        assertEquals(result ,resp );

    }

    @Test
    public void ApplyForJobUserNotFoundTest(){

        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> applicationService.applyForJob(jobId, null, email));
    }

    @Test
    public void ApplyForJobJobNotFoundTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> applicationService.applyForJob(jobId, null, email));
    }

    @Test
    public void ApplyForJobCandidateNotFoundTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user = User.builder().id(1L).build();
        Job job = Job.builder().createdBy(user).build();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        when(candidateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> applicationService.applyForJob(jobId, request, email));
    }

    @Test
    public void ApplyForJobUserIsNotJobOwnerTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Job job = Job.builder().createdBy(user1).build();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user2));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        assertThrows(UnauthorizedAccessException.class, () -> applicationService.applyForJob(jobId, request, email));
    }
    @Test
    public void ApplyForJobAlreadyAppliedTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user1 = User.builder().id(1L).build();
        Job job = Job.builder().createdBy(user1).build();
        Candidate candidate = new Candidate();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        when(candidateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(candidate));
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> applicationService.applyForJob(jobId, request, email));
    }

    @Test
    public void ApplyForJobApplicationSavedTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user1 = User.builder().id(1L).build();
        Job job = Job.builder().createdBy(user1).build();
        Candidate candidate = new Candidate();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        Application application = Application.builder().job(job).createdBy(user1).build();
        ApplicationResponse applicationResponse = ApplicationResponse.builder().id(1L).build();


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        when(candidateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(candidate));
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toResponse(any(Application.class))).thenReturn(applicationResponse);

        applicationService.applyForJob(jobId, request, email);
        Mockito.verify(applicationRepository).save(any(Application.class));
    }

    @Test
    public void ApplyForJobLogCreateTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user1 = User.builder().id(1L).build();
        Job job = Job.builder().createdBy(user1).build();
        Candidate candidate = new Candidate();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        Application application = Application.builder().job(job).createdBy(user1).build();
        ApplicationResponse applicationResponse = ApplicationResponse.builder().id(1L).build();


        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        when(candidateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(candidate));
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);
        when(applicationMapper.toResponse(any(Application.class))).thenReturn(applicationResponse);
        applicationService.applyForJob(jobId, request, email);
        verify(auditLogService).logCreate("Application", application.getId());
    }

    @Test
    public void ApplyForJobFineTest(){
        Long jobId = 1L;
        String email = "qwerty@gmail.com";
        User user1 = User.builder().id(1L).build();
        Job job = Job.builder().createdBy(user1).build();
        Candidate candidate = new Candidate();
        ApplyForJobRequest request = new ApplyForJobRequest();
        request.setCandidateId(1L);
        Application application = Application.builder().id(1L).job(job).createdBy(user1).build();
        ApplicationResponse applicationResponse = ApplicationResponse.builder().id(1L).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user1));
        when(jobRepository.findByIdAndIsActiveTrue(jobId)).thenReturn(Optional.of(job));
        when(candidateRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(candidate));
        when(applicationRepository.existsByJobIdAndCandidateId(jobId, candidate.getId())).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenReturn(application);

        when(applicationMapper.toResponse(any(Application.class))).thenReturn(applicationResponse);

        ApplicationResponse result = applicationService.applyForJob(jobId, request, email);
        assertEquals(result, applicationResponse);
    }



    @Test
    public void getJobCandidatesJobNotFoundTest(){
        Long JobId = 1L;
        when(jobRepository.existsById(JobId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> applicationService.getJobCandidates(JobId));
    }

    @Test
    public void getJobCandidatesFineTest(){
        Long JobId = 1L;

        Candidate candidate1 = Candidate.builder().id(1L).build();
        Candidate candidate2 = Candidate.builder().id(2L).build();

        List<Candidate> res1 = new ArrayList<>();
        res1.add(candidate1);
        res1.add(candidate2);

        CandidateResponse candidateResponse1 = CandidateResponse.builder().id(1L).build();
        CandidateResponse candidateResponse2 = CandidateResponse.builder().id(2L).build();

        List<CandidateResponse> res2 = new ArrayList<>();
        res2.add(candidateResponse1);
        res2.add(candidateResponse2);


        when(jobRepository.existsById(JobId)).thenReturn(true);
        when(applicationRepository.findCandidatesByJobId(JobId)).thenReturn(res1);
        when(candidateMapper.toResponse(candidate1)).thenReturn(candidateResponse1);
        when(candidateMapper.toResponse(candidate2)).thenReturn(candidateResponse2);
        assertEquals(applicationService.getJobCandidates(JobId), res2);
    }


    @Test
    public void getCandidateApplicationsCandidateNotFound(){
        Long candidateId = 1L;
        when(candidateRepository.existsById(candidateId)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class , ()-> applicationService.getCandidateApplications(candidateId));
    }

    @Test
    public void getCandidateApplicationsFineTest(){
        Long candidateId = 1L;

        Application application1 = Application.builder().id(1L).build();
        Application application2 = Application.builder().id(2L).build();

        List<Application> res1 = new ArrayList<>();
        res1.add(application1);
        res1.add(application2);

        ApplicationResponse applicationResponse1 = ApplicationResponse.builder().id(1L).build();
        ApplicationResponse applicationResponse2 = ApplicationResponse.builder().id(2L).build();

        List<ApplicationResponse> res2 = new ArrayList<>();
        res2.add(applicationResponse1);
        res2.add(applicationResponse2);

        when(applicationRepository.findAllByCandidateIdAndIsActiveTrue(candidateId)).thenReturn(res1);
        when(candidateRepository.existsById(candidateId)).thenReturn(true);
        when(applicationMapper.toResponse(application1)).thenReturn(applicationResponse1);
        when(applicationMapper.toResponse(application2)).thenReturn(applicationResponse2);
        assertEquals(applicationService.getCandidateApplications(candidateId), res2);

    }










}