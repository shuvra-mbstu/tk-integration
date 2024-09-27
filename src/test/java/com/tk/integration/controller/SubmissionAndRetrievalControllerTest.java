package com.tk.integration.controller;

import com.tk.integration.common.processor.CredentialsHandler;
import com.tk.integration.service.ExtractionService;
import com.tk.integration.service.SubmissionAndRetrievalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class SubmissionAndRetrievalControllerTest {

    @Mock
    private SubmissionAndRetrievalService submissionAndRetrievalService;

    @Mock
    private CredentialsHandler credentialsHandler;

    @Mock
    private ExtractionService extractionService;

    @InjectMocks
    private SubmissionAndRetrievalController submissionAndRetrievalController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(submissionAndRetrievalController).build();
    }

    @Test
    void testSubmitFileWithInvalidCredentials() throws IOException {
        // Mock MultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("uploaded_file", "testfile.txt",
                "text/plain", "Test file content".getBytes());

        // Convert MockMultipartFile to Resource
        Resource fileResource = new ByteArrayResource(mockFile.getBytes()) {
            @Override
            public String getFilename() {
                return mockFile.getOriginalFilename();
            }
        };

        // Create a MultiValueMap for multipart form-data
        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("uploaded_file", fileResource);  // Add file as a Resource
        formData.add("account", "testAccount");

        // Mock invalid credentials extraction
        when(credentialsHandler.extractCredentials(anyString())).thenReturn(null);

        // Simulate a multipart form-data submission using WebTestClient
        webTestClient.post()
                .uri("/api/submit")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Basic invalidBase64==")
                .bodyValue(formData)  // Send the multipart form data
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class);  // Expect a 400 Bad Request when credentials are invalid
    }

    @Test
    void testRetrieveFileResult() {
        // Mock the service call to return a result for a given process ID
        when(submissionAndRetrievalService.retrieveResult(anyString()))
                .thenReturn(Mono.just(ResponseEntity.ok("File processing result")));

        // Test the retrieve endpoint
        webTestClient.get()
                .uri("/api/retrieve/{processId}", "processId123")
                .header(HttpHeaders.AUTHORIZATION, "Basic dXNlcjpwYXNzd29yZA==")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("File processing result");
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
                .expectBody(String.class)
                .isEqualTo("Invalid processId");
    }
}
