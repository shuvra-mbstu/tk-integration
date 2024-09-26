package com.tk.integration.common.processor;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BinaryToMultipartFileProcessor {

    /**
     * Converts a byte array (binary data) into a MultipartFile object.
     * This is useful when you have raw binary data that needs to be processed as a file
     * (e.g., sending binary data to an external API as a file).
     *
     * @param binaryData The byte array representing the file's binary content.
     * @param fileName   The name of the file (this will be used as the file's name).
     * @return MultipartFile representing the binary data, which can be used in file upload scenarios.
     */
    public MultipartFile convertBytesToMultipartFile(byte[] binaryData, String fileName) {
        // Use a content type based on the file extension if needed, but default to "application/octet-stream"
        String contentType = determineContentType(fileName);

        // Create and return a MockMultipartFile with the binary data
        return new MockMultipartFile(
                fileName,                  // The name of the parameter being passed in HTTP request
                fileName,                  // The original file name (as it would appear on the user's system)
                contentType,               // The content type of the file (e.g., "application/octet-stream")
                binaryData                 // The actual binary content of the file
        );
    }

    /**
     * Determines the content type based on the file extension.
     * Currently defaults to "application/octet-stream" for unsupported file types.
     * You can extend this method to include more content types as needed.
     *
     * @param fileName The name of the file
     * @return String representing the file's MIME type
     */
    private String determineContentType(String fileName) {
        // Ensure the filename is not null or empty
        if (fileName == null || fileName.isBlank()) {
            return "application/octet-stream";  // Default content type
        }

        // Retrieve file extension
        String extension = getFileExtension(fileName).toLowerCase();

        // Simple content type mapping based on file extension
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "pdf":
                return "application/pdf";
            case "txt":
                return "text/plain";
            case "doc":
            case "docx":
                return "application/msword";
            default:
                return "application/octet-stream";  // Default to binary stream if unknown
        }
    }

    /**
     * Helper method to extract the file extension from a file name.
     *
     * @param fileName The full name of the file (with or without the extension)
     * @return The file extension, or an empty string if no extension is found
     */
    private String getFileExtension(String fileName) {
        // Find the last occurrence of '.' in the file name
        int lastDotIndex = fileName.lastIndexOf('.');

        // If there's no '.', or the dot is the last character, return an empty string
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }

        // Return the file extension
        return fileName.substring(lastDotIndex + 1);
    }
}
