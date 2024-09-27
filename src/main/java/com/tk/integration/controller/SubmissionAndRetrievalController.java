package com.tk.integration.controller;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.processor.CredentialsHandler;
import com.tk.integration.service.SubmissionAndRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import java.util.function.Supplier;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class SubmissionAndRetrievalController {

    private static final Logger logger = Logger.getLogger(SubmissionAndRetrievalController.class.getName());

    private final SubmissionAndRetrievalService submissionAndRetrievalService;
    private final CredentialsHandler credentialsHandler;

    @Autowired
    public SubmissionAndRetrievalController(SubmissionAndRetrievalService submissionAndRetrievalService, CredentialsHandler credentialsHandler) {
        this.submissionAndRetrievalService = submissionAndRetrievalService;
        this.credentialsHandler = credentialsHandler;
    }

    /**
     * Handles file submission and extracts credentials from the Authorization header.
     *
     * @param file     The file to be uploaded
     * @param account  The account identifier for the file
     * @param headers  HTTP headers containing Authorization information
     * @return Mono<ResponseEntity<String>> A Mono containing the process ID or an error response
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<String>> submit(@RequestParam("uploaded_file") @NonNull MultipartFile file,
                                               @RequestParam("account") @NonNull String account,
                                               @RequestHeader HttpHeaders headers) {

        // Extract and validate credentials from the Authorization header
        String[] credentials = credentialsHandler.extractCredentials(headers);
        if (credentials == null || credentials.length < 2) {
            logger.warning("Invalid or missing credentials");
            return Mono.just(ResponseEntity.badRequest().body(ApplicationConstant.INVALID_CREDENTIALS));
        }

        // Delegate the file submission to the service layer
        return submissionAndRetrievalService.submitFile(file, account, credentials[0], credentials[1]);
    }

    /**
     * Retrieves the result or status of a file processing based on process ID.
     *
     * @param processId The unique process ID to retrieve the status/result for
     * @return Mono<ResponseEntity<String>> A Mono containing the result or an error response
     */
    @GetMapping("/retrieve/{processId}")
    public Mono<ResponseEntity<String>> retrieve(@PathVariable String processId) {
        return submissionAndRetrievalService.retrieveResult(processId);
    }
}
