package com.tk.integration.service.serviceImpl;

import com.tk.integration.common.exception.TkIntegrationServerException;
import com.tk.integration.service.ExtractionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.tk.integration.common.processor.BinaryToMultipartFileProcessor;

@Service
public class ExtractionServiceImpl implements ExtractionService {

    // Logger to track the flow and errors
    private static final Logger logger = LoggerFactory.getLogger(ExtractionService.class);

    // WebClient for making asynchronous HTTP requests
    private final WebClient webClient;

    // Service to convert byte arrays to MultipartFile
    private final BinaryToMultipartFileProcessor binaryToMultipartFile;

    // Constructor-based dependency injection
    @Autowired
    public ExtractionServiceImpl(WebClient.Builder webClientBuilder, BinaryToMultipartFileProcessor binaryToMultipartFile) {
        // Base URL is set during WebClient initialization
        this.webClient = webClientBuilder.baseUrl("https://staging.textkernel.nl").build();
        this.binaryToMultipartFile = binaryToMultipartFile;
    }

    /**
     * Process a CV by sending it to the external service.
     *
     * @param file     The byte array of the file
     * @param fileName The name of the file
     * @param account  The associated account
     * @param username The username for authentication
     * @param password The password for authentication
     * @return Mono<String> containing the result from the external service or an error
     */
    @Override
    public Mono<String> processCV(byte[] file, String fileName, String account, String username, String password) {
        try {
            // Convert the byte array into a MultipartFile for sending as form data
            MultipartFile multipartFile = binaryToMultipartFile.convertBytesToMultipartFile(file, fileName);

            // Prepare the form data with required fields
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("account", account);
            formData.add("username", username);
            formData.add("password", password);
            formData.add("uploaded_file", multipartFile.getResource()); // Use the MultipartFile resource

            // Log the request for debugging
            logger.info("Form data prepared for processing CV: {}", fileName);

            // Send the POST request using WebClient and handle the response asynchronously
            return webClient.post()
                    .uri("/sourcebox/extract.do?useJsonErrorMsg=true") // Specify the API endpoint
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE) // Set content type
                    .bodyValue(formData) // Attach form data to the request body
                    .retrieve() // Retrieve the response
                    .bodyToMono(String.class) // Convert the response body to Mono<String>
                    .onErrorMap(e -> {
                        // Map errors to a custom exception and log the error message
                        logger.error("Error during CV processing: {}", e.getLocalizedMessage());
                        return TkIntegrationServerException.internalServerException(e.getMessage());
                    });

        } catch (Exception e) {
            // Handle any exception that occurs during the process and wrap it in a Mono error
            logger.error("Failed to process CV: {}", e.getMessage(), e);
            return Mono.error(TkIntegrationServerException.internalServerException(e.getMessage()));
        }
    }
}
