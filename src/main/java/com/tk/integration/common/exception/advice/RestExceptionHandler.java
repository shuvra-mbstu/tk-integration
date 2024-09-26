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
//
//    @ExceptionHandler(ConstraintViolationException.class)
//    public final ResponseEntity<Object>
//    handleConstraintViolationExceptionAllException(ConstraintViolationException ex, WebRequest request) {
//        ApiError apiError = new ApiError();
//        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
//        violations.forEach(violation -> {
//            TkIntegrationError tkIntegrationError = getTkIntegrationError(violation.getMessageTemplate());
//            apiError.addError(tkIntegrationError);
//        });
//        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(TkIntegrationServerException.class)
//    public final ResponseEntity<Object> handleIntegrationServerException(TkIntegrationServerException ex, WebRequest request) {
//        ApiError apiError = new ApiError();
//        TkIntegrationError tkIntegrationError = getTkIntegrationError(ex.getErrorId());
//        apiError.addError(tkIntegrationError);
//        return new ResponseEntity<>(apiError, ex.getStatus());
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//                                                                  HttpHeaders headers, HttpStatusCode status,
//                                                                  WebRequest request) {
//
//        ApiError apiError = new ApiError();
//        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
//            TkIntegrationError tkIntegrationError = getTkIntegrationError(error.getDefaultMessage(),
//                    buildErrorMessage(error));
//            apiError.addError(tkIntegrationError);
//        }
//        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
//                                                                  HttpHeaders headers, HttpStatusCode status,
//                                                                  WebRequest request) {
//        ApiError apiError = new ApiError();
//        if (e.getMostSpecificCause() instanceof TkIntegrationServerException tkIntegrationServerException) {
//            TkIntegrationError error = getTkIntegrationError(tkIntegrationServerException.getErrorId());
//            apiError.addError(error);
//            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//        } else if (e.getMostSpecificCause() instanceof InvalidFormatException) {
//            InvalidFormatException iex = (InvalidFormatException) e.getMostSpecificCause();
//            iex.getPath().forEach(reference -> {
//                TkIntegrationError tkIntegrationError = new TkIntegrationError(ErrorId.INVALID_DATA_FORMAT_EXCEPTION, iex.getOriginalMessage());
//                apiError.addError(tkIntegrationError);
//            });
//            return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
//        }
//        return handleAllExceptions(e, request);
//    }
//
//    private TkIntegrationError getTkIntegrationError(String code) {
//        TkIntegrationError tkIntegrationError = ErrorCodeReader.getTkIntegrationError(code);
//        if (Objects.isNull(tkIntegrationError)) {
//            return ErrorCodeReader.getErrorByMessage(code);
//        }
//        return tkIntegrationError;
//    }
//
//    private TkIntegrationError getTkIntegrationError(String code, String message) {
//        TkIntegrationError tkIntegrationError = ErrorCodeReader.getTkIntegrationError(code);
//        if (Objects.isNull(tkIntegrationError)) {
//            return ErrorCodeReader.getErrorByMessage(message);
//        }
//        return tkIntegrationError;
//    }
//
//    private String buildErrorMessage(FieldError error) {
//        return capitalize(StringUtils.join(splitByCharacterTypeCamelCase(emptyFieldErrorIfNull(error))
//        ), SPACE)) + SPACE + error.getDefaultMessage();
//    }

    private String emptyFieldErrorIfNull(FieldError fieldError) {
        return Objects.isNull(fieldError) ? ApplicationConstant.EMPTY_STRING : fieldError.getField();
    }
}