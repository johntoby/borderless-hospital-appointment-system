package com.borderless.hospital.exception;

/**
 * ResourceNotFoundException — thrown when a requested resource is not found.
 *
 * Examples:
 *   - Patient with ID 99 doesn't exist
 *   - Doctor with ID 5 doesn't exist
 *   - Appointment with ID 100 doesn't exist
 *
 * The GlobalExceptionHandler catches this and returns a 404 HTTP response.
 *
 * We extend RuntimeException so we don't need to declare it in method signatures.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
