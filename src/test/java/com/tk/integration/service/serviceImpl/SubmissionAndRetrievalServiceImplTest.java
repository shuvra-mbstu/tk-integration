package com.tk.integration.service.serviceImpl;

import com.tk.integration.common.processor.ResultProcessor;
import com.tk.integration.service.ExtractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SubmissionAndRetrievalServiceImplTest {

    private SubmissionAndRetrievalServiceImpl service;
    private ExtractionService extractionService;
    private ResultProcessor resultProcessor;

    @BeforeEach
    public void setup() {
        extractionService = mock(ExtractionService.class);
        resultProcessor = mock(ResultProcessor.class);
        service = new SubmissionAndRetrievalServiceImpl(extractionService, resultProcessor);
    }

    @Test
    public void testSubmitFileWithValidInput() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("testFile.txt");

        // Mock the async extraction service call
        when(extractionService.processCV(any(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just("Processing complete"));

        // Call the submitFile method
        Mono<ResponseEntity<String>> response = service.submitFile(file, "account123", "username", "password");

        // Verify that the response is OK
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.block().getStatusCode());
    }

    @Test
    public void testSubmitFileWithMissingFile() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        // Expecting a bad request error due to missing file
        Mono<ResponseEntity<String>> response = service.submitFile(file, "account123", "username", "password");

        assertThrows(Exception.class, () -> response.block());
    }

    @Test
    public void testRetrieveResultWithValidProcessId() {
        String processId = "process123";
        when(resultProcessor.retrieveResult(processId)).thenReturn("Completed");

        Mono<ResponseEntity<String>> response = service.retrieveResult(processId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.block().getStatusCode());
        assertEquals("Completed", response.block().getBody());
    }

    @Test
    public void testRetrieveResultWithInvalidProcessId() {
        String processId = "invalidProcessId";
        when(resultProcessor.retrieveResult(processId)).thenReturn(null);

        Mono<ResponseEntity<String>> response = service.retrieveResult(processId);

        assertThrows(Exception.class, () -> response.block());
    }
}