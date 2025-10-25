package com.financial.api.exception;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.financial.api.util.AppApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AppApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        Object target = ex.getBindingResult().getTarget();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String jsonFieldName = fieldName;

            try {
                if (target != null) {
                    // Use Jackson introspection to get the JSON property name from the target class
                    JavaType javaType = objectMapper.getTypeFactory().constructType(target.getClass());
                    BeanDescription beanDesc = objectMapper.getSerializationConfig().introspect(javaType);
                    List<BeanPropertyDefinition> properties = beanDesc.findProperties();

                    for (BeanPropertyDefinition prop : properties) {
                        if (prop.getInternalName().equals(fieldName)) {
                            jsonFieldName = prop.getName(); // This gets the JSON property name with snake_case
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // Fallback: manually convert camelCase to snake_case
                jsonFieldName = camelToSnakeCase(fieldName);
            }

            errors.put(jsonFieldName, error.getDefaultMessage());
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

    /**
     * Fallback method to convert camelCase to snake_case
     */
    private String camelToSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

}
