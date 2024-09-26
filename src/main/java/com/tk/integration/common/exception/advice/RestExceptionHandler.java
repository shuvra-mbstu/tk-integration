package com.tk.integration.common.exception.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tk.integration.common.constant.ApplicationConstant;
import com.tk.integration.common.constant.ErrorId;
import com.tk.integration.common.exception.TkIntegrationServerException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        TkIntegrationServerException tkIntegrationError =
                new TkIntegrationServerException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());

        return new ResponseEntity<>(tkIntegrationError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TkIntegrationServerException.class)
    public final ResponseEntity<Object> handleIntegrationServerException(TkIntegrationServerException ex) {
        return new ResponseEntity<>(ex.getLocalizedMessage(), ex.getStatus());
    }
}