package com.tk.integration.common.processor;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

@Service
public class CredentialsHandler {

    // Logger to capture important information or warnings during execution
    private static final Logger logger = Logger.getLogger(CredentialsHandler.class.getName());

    /**
     * Decodes the Basic Authorization header into username and password.
     *
     * @param header The Basic Authorization header value (e.g., "Basic dXNlcm5hbWU6cGFzc3dvcmQ=")
     * @return String[] Array containing the username and password, or null if decoding fails
     */
    public String[] decodeBasicAuthHeader(String header) {
        try {
            // Ensure the header contains the expected prefix
            if (header == null || !header.startsWith("Basic ")) {
                logger.warning("Invalid Basic Authorization header format.");
                return null;
            }

            // Extract the Base64-encoded credentials by removing the "Basic " prefix
            String base64Credentials = header.substring("Basic ".length()).trim();

            // Decode the Base64-encoded string
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);

            // Convert the decoded byte array into a UTF-8 string (username:password format)
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            // Split the decoded string into username and password
            return decodedString.split(":", 2); // Split into exactly 2 parts (username and password)
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            // Log a warning if the decoding fails for any reason
            logger.warning("Error decoding Basic Authorization header: " + e.getMessage());
            return null;
        }
    }

    /**
     * Extracts and decodes the Basic Authorization credentials from the HTTP headers.
     *
     * @param headers HTTP headers containing the Authorization information
     * @return String[] A decoded array containing the username and password, or null if the header is invalid
     */
    public String[] extractCredentials(HttpHeaders headers) {
        // Get the Authorization header from the HttpHeaders
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

        // If the Authorization header is missing or doesn't start with "Basic ", return null
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warning("Authorization header is missing or does not contain Basic authentication.");
            return null;
        }

        // Delegate the actual decoding of the header to the decodeBasicAuthHeader method
        return decodeBasicAuthHeader(authHeader);
    }
}
