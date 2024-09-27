package com.tk.integration.common.processor;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.exception.TkIntegrationServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsHandlerTest {

    private CredentialsHandler credentialsHandler;

    @BeforeEach
    void setUp() {
        credentialsHandler = new CredentialsHandler();
    }

    @Test
    void testDecodeValidBasicAuthHeader() {
        // Basic Authorization header for "username:password" encoded in Base64
        String validHeader = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";  // "username:password" in base64

        // Act
        String[] credentials = credentialsHandler.decodeBasicAuthHeader(validHeader);

        // Assert
        assertNotNull(credentials);
        assertEquals("username", credentials[0]);
        assertEquals("password", credentials[1]);
    }

    @Test
    void testDecodeInvalidBasicAuthHeader() {
        // Invalid Base64-encoded string
        String invalidHeader = "Basic invalidBase64==";

        // Act
        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(invalidHeader);
        });
        // Assert
        assertNotNull(invalidHeader, "Last unit does not have enough valid bits");
    }

    @Test
    void testDecodeMissingBasicPrefix() {
        // Header without "Basic " prefix
        String headerWithoutBasic = "dXNlcm5hbWU6cGFzc3dvcmQ=";

        // Act
        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(headerWithoutBasic);
        });
        // Assert
        assertNotNull(headerWithoutBasic, "Should return null for headers without 'Basic ' prefix.");
    }

    @Test
    void testDecodeBasicAuthHeaderWithInvalidFormat() {
        // A header with a valid Base64 but invalid format (no colon)
        String invalidHeader = "Basic:";  // "username password" instead of "username:password"
        // Act & Assert
        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(invalidHeader);
        });

        assertEquals(ApplicationConstant.INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void testExtractValidCredentials() {
        // Valid Basic Authorization header
        String validHeader = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";

        // Act
        String[] credentials = credentialsHandler.extractCredentials(validHeader);

        // Assert
        assertNotNull(credentials);
        assertEquals("username", credentials[0]);
        assertEquals("password", credentials[1]);
    }

    @Test
    void testExtractCredentialsWithMissingHeader() {
        // Missing Authorization header
        String missingHeader = null;

        // Act & Assert
        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(missingHeader);
        });

        assertEquals(ApplicationConstant.INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void testExtractCredentialsWithInvalidPrefix() {
        // Authorization header without "Basic " prefix
        String invalidPrefixHeader = "Bearer dXNlcm5hbWU6cGFzc3dvcmQ=";

        // Act & Assert
        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(invalidPrefixHeader);
        });

        assertEquals(ApplicationConstant.INVALID_CREDENTIALS, exception.getMessage());
    }

    @Test
    void testExtractCredentialsWithInvalidBase64() {
        // Invalid Base64 in Authorization header
        String invalidBase64Header = "Basic invalidBase64==";

        TkIntegrationServerException exception = assertThrows(TkIntegrationServerException.class, () -> {
            credentialsHandler.extractCredentials(invalidBase64Header);
        });

        assertEquals("Last unit does not have enough valid bits", exception.getMessage());
    }
}
