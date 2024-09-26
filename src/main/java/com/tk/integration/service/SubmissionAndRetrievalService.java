package com.tk.integration.service;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.service.StorageService;
import com.tk.integration.common.exception.TkIntegrationServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class SubmissionAndRetrievalService {

    // For testing or checking purposes, we can expose the resultStore (Optional)

    private final ExtractionService extractionService;
    private final StorageService storageService;

    @Autowired
    public SubmissionAndRetrievalService(ExtractionService extractionService, StorageService storageService) {
        this.extractionService = extractionService;
        this.storageService = storageService;
    }

    // Handles submission of file and process ID generation
    public Mono<ResponseEntity<String>> submitFile(MultipartFile file, String account, String username, String password) {
        String processId = UUID.randomUUID().toString();  // Generate a random UUID for the process ID
        storageService.storeResult(processId, ApplicationConstant.STATUS);
        try {
            // Specify a custom directory
            File directory = new File("../tk-integration/tmp");

            // Create the directory if it doesn't exist
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Create a temporary file in the specified directory
            File tempFile = File.createTempFile(Objects.requireNonNull(file.getOriginalFilename()), ".tmp", directory);
//            file.transferTo(tempFile);

            // Print the path of the temporary file
            System.out.println("Temporary file created at: " + tempFile.getAbsolutePath());

            // Optionally, delete the file when the JVM exits
            processJobAsync(processId, tempFile, account, username, password);
//        return extractionService.processCV(file, account, username, password)
//                .map(result -> {
//                    resultStore.put(processId, result);  // Store the result once ready
//                    return ResponseEntity.ok(processId);
//                });

//            tempFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("processId: " + processId);
        return Mono.just(ResponseEntity.ok(processId));
    }

    @Async
    public void processJobAsync(String processId, File file, String account, String username, String password) {
        try {
//            UUID fileId = storageService.storeFile(file);

            // Simulate a long-running background task
            extractionService.processCV(file, account, username, password)
                    .doOnNext(r -> storageService.storeResult(processId, r))
                    .doOnError(e -> storageService.storeResult(processId, e.getLocalizedMessage())).block();
//                    .subscribe(r -> System.out.println("Result: " + r));
            System.out.println("processId: #### " + processId);
        } catch (Exception e) {
            // In case of an error, store an error message
            throw new TkIntegrationServerException(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    // Retrieve the current status or result of the file processing
    public Mono<ResponseEntity<String>> retrieveResult(String processId) {
        String result = storageService.retrieveResult(processId);
        if (result == null) {
            return Mono.just(ResponseEntity.status(404).body("Invalid processId"));
        } else {
            return Mono.just(ResponseEntity.ok(result));
        }
    }
}