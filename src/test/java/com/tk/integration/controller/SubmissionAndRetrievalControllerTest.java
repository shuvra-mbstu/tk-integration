package com.tk.integration.controller;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.processor.CredentialsHandler;
import com.tk.integration.service.SubmissionAndRetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class SubmissionAndRetrievalControllerTest {

    @Mock
    private SubmissionAndRetrievalService submissionAndRetrievalService;

    @Mock
    private CredentialsHandler credentialsHandler;

    @InjectMocks
    private SubmissionAndRetrievalController submissionAndRetrievalController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Set up WebTestClient with the controller
        webTestClient = WebTestClient.bindToController(submissionAndRetrievalController).build();
    }

    @Test
    void testSubmitFileWithValidCredentials() {
        // Create a mock MultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("uploaded_file", "testfile.txt", "text/plain", "Test file content".getBytes());

        // Create a MultiValueMap to hold the multipart form data
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("uploaded_file", mockFile);  // Convert MockMultipartFile to Resource
        formData.add("account", "testAccount");

        // Mock valid credentials extraction
        when(credentialsHandler.extractCredentials(any(HttpHeaders.class)))
                .thenReturn(new String[]{"username", "password"});

        // Mock service response for successful submission
        when(submissionAndRetrievalService.submitFile(any(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok("processId123")));

        // Perform the multipart form-data submission using WebTestClient
        webTestClient.post()
                .uri("/api/submit")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")  // "username:password" in base64
                .body(BodyInserters.fromMultipartData(formData))  // Send the multipart form data
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("processId123");
    }

    @Test
    void testSubmitFileWithInvalidCredentials() {
        // Mock Authorization header with invalid credentials
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Basic 64==");

        // Mock credentials extraction failure
        when(credentialsHandler.extractCredentials(any(HttpHeaders.class))).thenReturn(null);

        // Test the submit endpoint
        webTestClient.post()
                .uri("/api/submit")
                .header(HttpHeaders.AUTHORIZATION, "Basic 64==")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testRetrieveFileResult() {
        // Mock the service call to return a result for a given process ID
        when(submissionAndRetrievalService.retrieveResult(anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok("File processing result")));

        // Test the retrieve endpoint
        webTestClient.get()
                .uri("/api/retrieve/{processId}", "processId123")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("File processing result");
    }

    @Test
    void testRetrieveFileResultNotFound() {
        // Mock the service call to return a 404 Not Found for an invalid process ID
        when(submissionAndRetrievalService.retrieveResult(anyString()))
                .thenReturn(Mono.just(ResponseEntity.status(404).body("Invalid processId")));

        // Test the retrieve endpoint
        webTestClient.get()
                .uri("/api/retrieve/{processId}", "invalidProcessId")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class).isEqualTo("Invalid processId");
    }
}
