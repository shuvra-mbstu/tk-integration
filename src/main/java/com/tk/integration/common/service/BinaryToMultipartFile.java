package com.tk.integration.common.service;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BinaryToMultipartFile {

    // Convert a byte array (binary data) to MultipartFile
    public MultipartFile convertBytesToMultipartFile(byte[] binaryData, String fileName) {
        return new MockMultipartFile(
                fileName,                  // The name of the file
                fileName,                  // The original file name in the user's system
                "application/octet-stream", // Content type
                binaryData                 // The binary data
        );
    }

    // Convert an InputStream to MultipartFile (another way to handle binary data)
    public MultipartFile convertInputStreamToMultipartFile(ByteArrayInputStream inputStream, String fileName) throws IOException {
        return new MockMultipartFile(
                fileName,                  // The name of the file
                fileName,                  // The original file name in the user's system
                "application/octet-stream", // Content type
                inputStream                // The InputStream containing the binary data
        );
    }
}