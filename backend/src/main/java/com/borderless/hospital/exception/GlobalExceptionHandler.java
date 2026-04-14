package com.borderless.hospital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler — centralized error handling for all controllers.
 *
 * HOW IT WORKS:
 *   @RestControllerAdvice tells Spring to apply this class globally.
 *   Each @ExceptionHandler method intercepts a specific exception type
 *   and returns a structured JSON error response instead of crashing.
 *
 * WITHOUT THIS:
 *   Spring would return a generic 500 error with a stack trace — ugly!
 *
 * WITH THIS:
 *   Every error returns a clean JSON like: {"error": "Email already registered"}
 *
 * This implements the "fail gracefully" principle in API design.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 404 — Resource not found
     * Triggered by: ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles 400 — Bad request / invalid input
     * Triggered by: BadRequestException
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles 400 — Validation failures from @Valid
     * Triggered when a @NotBlank, @Email, @Size etc. constraint is violated.
     *
     * Returns a map of field names to their error messages:
     * { "email": "Please provide a valid email", "password": "Password required" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles 500 — Unexpected server errors (catch-all)
     * Prevents stack traces from leaking to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "An unexpected error occurred. Please try again.");
        // In a real app, log the actual exception here (not expose to client)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
