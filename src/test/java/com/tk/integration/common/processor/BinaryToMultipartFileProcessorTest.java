package com.tk.integration.common.processor;

import com.tk.integration.common.exception.TkIntegrationServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BinaryToMultipartFileProcessorTest {

    private BinaryToMultipartFileProcessor processor;

    @BeforeEach
    void setUp() {
        // Initialize the processor before each test
        processor = new BinaryToMultipartFileProcessor();
    }

    @Test
    void testConvertBytesToMultipartFile() throws IOException {
        // Sample binary data (representing file content)
        byte[] binaryData = "This is test data.".getBytes();

        // Expected file name
        String fileName = "testFile.txt";

        // Convert the binary data to MultipartFile
        MultipartFile multipartFile = processor.convertBytesToMultipartFile(binaryData, fileName);

        // Assert that the result is not null
        assertNotNull(multipartFile, "The converted MultipartFile should not be null.");

        // Assert that the file name is correct
        assertEquals(fileName, multipartFile.getOriginalFilename(), "The file name should match the input file name.");

        // Assert that the content type is "application/octet-stream"
        assertEquals("text/plain", multipartFile.getContentType(), "The content type should default to 'application/octet-stream'.");

        // Assert that the content matches the binary data
        assertArrayEquals(binaryData, multipartFile.getBytes(), "The content of the file should match the binary data.");
    }

    @Test
    void testConvertBytesToMultipartFileWithEmptyData() {
        // Empty binary data
        byte[] emptyData = new byte[0];

        // Expected file name
        String fileName = "emptyFile.txt";

        // Convert the empty data to MultipartFile
        MultipartFile multipartFile = processor.convertBytesToMultipartFile(emptyData, fileName);

        // Assert that the result is not null
        assertNotNull(multipartFile, "The converted MultipartFile should not be null.");

        // Assert that the file name is correct
        assertEquals(fileName, multipartFile.getOriginalFilename(), "The file name should match the input file name.");

        // Assert that the content is empty
        assertEquals(0, multipartFile.getSize(), "The size of the file should be 0.");
    }

    @Test
    void testConvertBytesToMultipartFileWithNullData() {
        // Null binary data
        byte[] nullData = null;

        // Expected file name
        String fileName = "nullFile.txt";

        // Convert the null data to MultipartFile (should throw NullPointerException)
        assertThrows(TkIntegrationServerException.class, () -> processor.convertBytesToMultipartFile(null, fileName));
    }
}
