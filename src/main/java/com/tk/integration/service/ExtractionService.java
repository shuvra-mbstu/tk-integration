package com.tk.integration.service;

import com.tk.integration.common.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
public class ExtractionService {

    private final WebClient webClient;
    private final StorageService storageService;

    @Autowired
    public ExtractionService(WebClient.Builder webClientBuilder, StorageService storageService) {
        this.webClient = webClientBuilder.baseUrl("https://staging.textkernel.nl").build();
        this.storageService = storageService;
    }

    public Mono<String> processCV(File file, String account, String username, String password) {
        try {
//            File tempFile = filepath.
//            UUID fileId = storageService.storeFile(new MultipartFile(tempFile));
//            MultipartFile file = storageService.retrieveFile(fileId);
//            System.out.println("file: " + file.getResource().getInputStream());
//            Resource resource = new ClassPathResource(Objects.requireNonNull(file.getOriginalFilename()), file.getInputStream().getClass());

            // Build the multipart form data
            MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
            formData.add("account", account);
            formData.add("username", username);
            formData.add("password", password);
            formData.add("uploaded_file", new FileSystemResource(file));

            System.out.println("formData: " + formData);
            return webClient.post()
                    .uri("/sourcebox/extract.do?useJsonErrorMsg=true")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(String.class); // Return the result as Mono<String>
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to process CV", e.getCause()));
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