package com.example.batchprocessing;

import com.example.batchprocessing.domain.BatchJobResult;
import com.example.batchprocessing.service.BatchProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
public class BatchProcessingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BatchProcessingService batchProcessingService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/batch/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Batch Processing Service is healthy"));
    }

    @Test
    void testStartBatchJob() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("jobName", "testJob");
        request.put("jobType", "dataProcessing");

        mockMvc.perform(post("/batch/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void testStartBatchJobValidation() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("jobName", ""); // 빈 jobName으로 유효성 검사 테스트

        mockMvc.perform(post("/batch/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetJobStatus() throws Exception {
        // First start a job
        Map<String, String> request = new HashMap<>();
        request.put("jobName", "statusTestJob");
        request.put("jobType", "dataProcessing");

        BatchJobResult startedJob = batchProcessingService.startBatchJob("statusTestJob");

        mockMvc.perform(get("/batch/status/{jobId}", startedJob.getJobId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(startedJob.getJobId()))
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void testGetNonExistentJobStatus() throws Exception {
        mockMvc.perform(get("/batch/status/nonexistent-job-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCancelJob() throws Exception {
        // First start a job
        Map<String, String> request = new HashMap<>();
        request.put("jobName", "cancelTestJob");
        request.put("jobType", "dataProcessing");

        BatchJobResult startedJob = batchProcessingService.startBatchJob("cancelTestJob");

        mockMvc.perform(delete("/batch/cancel/{jobId}", startedJob.getJobId()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Job cancellation requested")));
    }

    @Test
    void testGetAllJobs() throws Exception {
        mockMvc.perform(get("/batch/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 