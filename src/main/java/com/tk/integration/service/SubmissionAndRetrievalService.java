package com.tk.integration.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

public interface SubmissionAndRetrievalService {
    public Mono<ResponseEntity<String>> submitFile(MultipartFile file, String account, String username, String password);

    public Mono<ResponseEntity<String>> retrieveResult(String processId);
}

