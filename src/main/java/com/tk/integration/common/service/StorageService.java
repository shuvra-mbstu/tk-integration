package com.tk.integration.common.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StorageService {

    private final Map<UUID, MultipartFile> fileStorage = new ConcurrentHashMap<>(); // In-memory storage

    private final Map<String, String> resultStore = new ConcurrentHashMap<>();

    public UUID storeFile(MultipartFile file) throws IOException {
        UUID processId = UUID.randomUUID(); // Generate a unique ID
        fileStorage.put(processId, file); // Store file bytes in memory
        return processId; // Return the process ID for future reference
    }

    public void storeResult(String processId, String result) {
        resultStore.put(processId, result);
    }
    public String retrieveResult(String processId) {
        return resultStore.get(processId);
    }

    public MultipartFile retrieveFile(UUID processId) {
        return fileStorage.get(processId); // Retrieve file bytes using process ID
    }
}