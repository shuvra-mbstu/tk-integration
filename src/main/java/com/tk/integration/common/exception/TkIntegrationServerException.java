package com.tk.integration.common.exception;

import com.tk.integration.common.constant.ApplicationConstant;
import lombok.Data;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Data
public class TkIntegrationServerException extends RuntimeException {
    private static final long serialVersionUID = 1436995162658277359L;
    private final String message;
    private HttpStatus status;

    public TkIntegrationServerException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static TkIntegrationServerException badRequest(String message) {
        return new TkIntegrationServerException(HttpStatus.BAD_REQUEST, message);
    }

    public static TkIntegrationServerException notFound(String message) {
        return new TkIntegrationServerException(HttpStatus.NOT_FOUND, message);
    }

    public static TkIntegrationServerException dataSaveException(String message) {
        return new TkIntegrationServerException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static TkIntegrationServerException internalServerException(String message) {
        return new TkIntegrationServerException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public static TkIntegrationServerException notAuthorized(String message) {
        return new TkIntegrationServerException(HttpStatus.UNAUTHORIZED, message);
    }
}
