package com.tk.integration.common.processor;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import static org.junit.jupiter.api.Assertions.*;

public class CredentialsHandlerTest {

    private final CredentialsHandler credentialsHandler = new CredentialsHandler();

    @Test
    public void testDecodeBasicAuthHeader() {
        // Base64 encoded for "username:password"
        String encodedAuth = "Basic dXNlcm5hbWU6cGFzc3dvcmQ=";

        String[] credentials = credentialsHandler.decodeBasicAuthHeader(encodedAuth);

        assertNotNull(credentials);
        assertEquals("username", credentials[0]);
        assertEquals("password", credentials[1]);
    }

    @Test
    public void testDecodeBasicAuthHeaderWithInvalidFormat() {
        // Invalid header format (missing Basic)
        String invalidAuth = "dXNlcm5hbWU6cGFzc3dvcmQ=";

        String[] credentials = credentialsHandler.decodeBasicAuthHeader(invalidAuth);

        assertNull(credentials); // Expect null due to missing Basic prefix
    }

    @Test
    public void testExtractCredentialsWithValidHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic dXNlcm5hbWU6cGFzc3dvcmQ=");

        String[] credentials = credentialsHandler.extractCredentials(headers);

        assertNotNull(credentials);
        assertEquals("username", credentials[0]);
        assertEquals("password", credentials[1]);
    }

    @Test
    public void testExtractCredentialsWithMissingAuthorization() {
        HttpHeaders headers = new HttpHeaders(); // Empty headers

        String[] credentials = credentialsHandler.extractCredentials(headers);

        assertNull(credentials); // Expect null for missing Authorization header
    }

    @Test
    public void testExtractCredentialsWithInvalidAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer invalid_token");

        String[] credentials = credentialsHandler.extractCredentials(headers);

        assertNull(credentials); // Expect null for invalid authorization scheme
    }
}
