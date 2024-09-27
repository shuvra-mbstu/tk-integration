package com.tk.integration.common.processor;

import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.exception.TkIntegrationServerException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
            // Extract the Base64-encoded credentials by removing the "Basic " prefix
            String base64Credentials = header.substring("Basic ".length()).trim();

            // Decode the Base64-encoded string
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);

            // Convert the decoded byte array into a UTF-8 string (username:password format)
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            // Split the decoded string into username and password
            String[] credentials = StringUtils.split(decodedString, ":");

            // Validate the extracted credentials
            if (credentials == null || credentials.length != 2 || StringUtils.isEmpty(credentials[0]) || StringUtils.isEmpty(credentials[1])) {
                logger.warning("Invalid credentials in Basic Authorization header: " + decodedString);
                throw TkIntegrationServerException.notAuthorized(ApplicationConstant.INVALID_CREDENTIALS);
            }

            return credentials;
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            // Log a warning if the decoding fails for any reason
            logger.warning("Error decoding Basic Authorization header: " + e.getMessage());
            throw TkIntegrationServerException.internalServerException(e.getMessage());
        }
    }

    /**
     * Extracts and decodes the Basic Authorization credentials from the HTTP headers.
     *
     * @param authorizationString HTTP headers containing the Authorization information
     * @return String[] A decoded array containing the username and password, or null if the header is invalid
     */
    public String[] extractCredentials(String authorizationString) {
        if (authorizationString == null || !authorizationString.startsWith("Basic ")) {
            logger.warning("Authorization header is missing or does not contain Basic authentication." + authorizationString);
            throw TkIntegrationServerException.notAuthorized(ApplicationConstant.INVALID_CREDENTIALS);
        }

        // Delegate the actual decoding of the header to the decodeBasicAuthHeader method
        return decodeBasicAuthHeader(authorizationString);
    }
}