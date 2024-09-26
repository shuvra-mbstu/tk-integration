package com.tk.integration.service.serviceImpl;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.exception.TkIntegrationServerException;
import com.tk.integration.common.processor.ResultProcessor;
import com.tk.integration.service.ExtractionService;
import com.tk.integration.service.SubmissionAndRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class SubmissionAndRetrievalServiceImpl implements SubmissionAndRetrievalService {

    private static final Logger logger = Logger.getLogger(SubmissionAndRetrievalService.class.getName());

    private final ExtractionService extractionService;
    private final ResultProcessor resultProcessor;

    @Autowired
    public SubmissionAndRetrievalServiceImpl(ExtractionService extractionService, ResultProcessor resultProcessor) {
        this.extractionService = extractionService;
        this.resultProcessor = resultProcessor;
    }

    /**
     * Handles submission of file and process ID generation.
     * Validates the input and starts asynchronous job processing.
     *
     * @param file the file to be processed
     * @param account the account associated with the submission
     * @param username the username for the submission
     * @param password the password for the submission
     * @return Mono<ResponseEntity<String>> a Mono containing the process ID
     */
    @Override
    public Mono<ResponseEntity<String>> submitFile(MultipartFile file, String account, String username, String password) {
        // Validate that the file is not empty
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            logger.warning("File is empty or has no original filename");
            return Mono.error(TkIntegrationServerException.badRequest(ApplicationConstant.FILE_MISSING));
        }

        // Validate account string
        if (account == null || account.isBlank()) {
            logger.warning("Account is missing or blank");
            return Mono.error(TkIntegrationServerException.badRequest(ApplicationConstant.ACCOUNT_MISSING));
        }

        // Generate a unique process ID
        String processId = UUID.randomUUID().toString();

        // Store the initial status for the process
        resultProcessor.storeResult(processId, ApplicationConstant.STATUS);

        try {
            // Asynchronously process the file
            processJobAsync(processId, file.getBytes(), file.getOriginalFilename(), account, username, password);
        } catch (IOException e) {
            logger.severe("Error reading file: " + e.getMessage());
            throw new TkIntegrationServerException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing file");
        }

        // Return the process ID immediately
        return Mono.just(ResponseEntity.ok(processId));
    }

    /**
     * Asynchronous processing of the submitted file.
     * Stores the result or error associated with the process ID.
     *
     * @param processId the generated process ID for tracking
     * @param file the byte array of the file
     * @param filename the name of the file
     * @param account the associated account
     * @param username the username for processing
     * @param password the password for processing
     */
    @Async
    protected void processJobAsync(String processId, byte[] file, String filename, String account, String username, String password) {
        // Handle the extraction process asynchronously
        extractionService.processCV(file, filename, account, username, password)
                .doOnSuccess(result -> {
                    // Store the result upon successful processing
                    logger.info("Process " + processId + " completed successfully.");
                    resultProcessor.storeResult(processId, result);
                })
                .doOnError(error -> {
                    // Log and store the error message if processing fails
                    String errorMessage = "Process " + processId + " failed: " + error.getMessage();
                    logger.severe(errorMessage);
                    resultProcessor.storeResult(processId, errorMessage);
                })
                .subscribe(); // Subscribe to trigger the processing
    }

    /**
     * Retrieve the current status or result of the file processing.
     *
     * @param processId the process ID to retrieve the status/result for
     * @return Mono<ResponseEntity<String>> containing the current result or status
     */
    @Override
    public Mono<ResponseEntity<String>> retrieveResult(String processId) {
        // Retrieve the result associated with the processId
        String result = resultProcessor.retrieveResult(processId);

        // Check if a result was found
        if (result == null) {
            logger.warning("No result found for processId: " + processId);
            return Mono.error(TkIntegrationServerException.notFound(ApplicationConstant.PROCESS_NOT_FOUND + processId));
        } else {
            // Return the result if found
            return Mono.just(ResponseEntity.ok(result));
        }
    }
}