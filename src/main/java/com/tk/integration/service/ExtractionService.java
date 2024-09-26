package com.tk.integration.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ExtractionService {
    public Mono<String> processCV(byte[] file, String fileName, String account, String username, String password);
}

