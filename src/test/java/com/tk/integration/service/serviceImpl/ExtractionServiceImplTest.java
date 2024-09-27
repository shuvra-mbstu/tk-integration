package com.tk.integration.service.serviceImpl;

import com.tk.integration.common.processor.BinaryToMultipartFileProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ExtractionServiceImplTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private BinaryToMultipartFileProcessor binaryToMultipartFileProcessor;

    @InjectMocks
    private ExtractionServiceImpl extractionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
    }

    @Test
    void testProcessCVSuccess() {
        // Given
        byte[] fileBytes = "test file data".getBytes(StandardCharsets.UTF_8);
        String fileName = "testFile.txt";
        String account = "testAccount";
        String username = "testUsername";
        String password = "testPassword";
        MultipartFile mockMultipartFile = new MockMultipartFile(fileName, fileName, MediaType.TEXT_PLAIN_VALUE, fileBytes);

        // Mock the binaryToMultipartFile conversion
        when(binaryToMultipartFileProcessor.convertBytesToMultipartFile(any(byte[].class), eq(fileName)))
                .thenReturn(mockMultipartFile);

        // Mock WebClient flow
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(eq(HttpHeaders.CONTENT_TYPE), eq(MediaType.MULTIPART_FORM_DATA_VALUE)))
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Processing Successful"));

        // When
        Mono<String> result = extractionService.processCV(fileBytes, fileName, account, username, password);

        // Then
        StepVerifier.create(result)
                .expectNext("Processing Successful")
                .verifyComplete();

        verify(binaryToMultipartFileProcessor, times(1))
                .convertBytesToMultipartFile(fileBytes, fileName);
        verify(webClient, times(1)).post();
    }

    @Test
    void testProcessCVError() {
        // Given
        byte[] fileBytes = "test file data".getBytes(StandardCharsets.UTF_8);
        String fileName = "testFile.txt";
        String account = "testAccount";
        String username = "testUsername";
        String password = "testPassword";
        MultipartFile mockMultipartFile = new MockMultipartFile(fileName, fileName, MediaType.TEXT_PLAIN_VALUE, fileBytes);

        // Mock the binaryToMultipartFile conversion
        when(binaryToMultipartFileProcessor.convertBytesToMultipartFile(any(byte[].class), eq(fileName)))
                .thenReturn(mockMultipartFile);

        // Mock WebClient flow to simulate an error
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(eq(HttpHeaders.CONTENT_TYPE), eq(MediaType.MULTIPART_FORM_DATA_VALUE)))
                .thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new WebClientResponseException(500, "Internal Server Error", null, null, null)));

        // When
        Mono<String> result = extractionService.processCV(fileBytes, fileName, account, username, password);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof WebClientResponseException &&
                        ((WebClientResponseException) throwable).getStatusCode().value() == 500)
                .verify();

        verify(binaryToMultipartFileProcessor, times(1))
                .convertBytesToMultipartFile(fileBytes, fileName);
        verify(webClient, times(1)).post();
    }
}