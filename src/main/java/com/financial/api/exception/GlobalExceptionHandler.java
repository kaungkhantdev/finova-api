package com.financial.api.exception;

import com.financial.api.util.AppApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        AppApiResponse<Map<String, String>> errorResponse = AppApiResponse.error("Validation failed", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<AppApiResponse<String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "You do not have permission to access this resource");
        AppApiResponse<String> errorResponse = AppApiResponse.error("You do not have permission to access this resource");
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN); // 403 Forbidden
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AppApiResponse<String>> handleGeneralException(Exception ex) {
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        AppApiResponse<String> response = AppApiResponse.error(rootCause.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppApiResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable rootCause = ex.getCause() != null ? ex.getCause() : ex;
        AppApiResponse<String> response = AppApiResponse.error(rootCause.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
