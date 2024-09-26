package com.tk.integration.controller;

import com.tk.integration.service.SubmissionAndRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class SubmissionAndRetrievalController {

    private final SubmissionAndRetrievalService submissionAndRetrievalService;

    @Autowired
    public SubmissionAndRetrievalController(SubmissionAndRetrievalService submissionAndRetrievalService) {
        this.submissionAndRetrievalService = submissionAndRetrievalService;
    }

    @PostMapping("/submit")
    public Mono<ResponseEntity<String>> submit(@RequestParam("uploaded_file") MultipartFile file,
                                               @RequestParam("account") String account,
                                               @RequestParam("username") String username,
                                               @RequestParam("password") String password) {

       return submissionAndRetrievalService.submitFile(file, account, username, password);
    }

    @GetMapping("/retrieve/{processId}")
    public Mono<ResponseEntity<String>> retrieve(@PathVariable String processId) {
        return submissionAndRetrievalService.retrieveResult(processId);
    }
}