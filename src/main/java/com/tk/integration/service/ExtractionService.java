package com.tk.integration.service;

import com.tk.integration.common.constant.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.BitSet;
import java.util.UUID;

@Service
public class ExtractionService {

    private final WebClient webClient;
    private final StorageService storageService;

    @Autowired
    public ExtractionService(WebClient.Builder webClientBuilder, StorageService storageService) {
        this.webClient = webClientBuilder.baseUrl("https://staging.textkernel.nl").build();
        this.storageService = storageService;
    }

    public Mono<String> processCV(MultipartFile file, String account, String username, String password) {
        try {
//            MultipartFile file = storageService.retrieveFile(fileId);
//            System.out.println("file: " + file.getResource().getInputStream());
//            Resource resource = new ClassPathResource(Objects.requireNonNull(file.getOriginalFilename()), file.getInputStream().getClass());

            // Build the multipart form data
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("account", account);
            formData.add("username", username);
            formData.add("password", password);
            formData.add("uploaded_file", file.getResource());

            System.out.println("formData: " + formData);
            return webClient.post()
                    .uri("/sourcebox/extract.do?useJsonErrorMsg=true")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class); // Return the result as Mono<String>
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process CV", e));
        }
    }

//    public File fetchFileFromTempDirectory(String filePath) throws IOException {
//        // Convert the string file path into a Path object
//        Path tempFilePath = Paths.get(filePath);
//
//        // Check if the file exists
//        if (Files.exists(tempFilePath)) {
//            // Return the file if it exists
//            return tempFilePath.toFile();
//        } else {
//            throw new IOException("File not found: " + filePath);
//        }
//    }
}