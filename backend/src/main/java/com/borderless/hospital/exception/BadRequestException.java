package com.borderless.hospital.exception;

/**
 * BadRequestException — thrown when the client sends invalid input.
 *
 * Examples:
 *   - Wrong password at login
 *   - A patient tries to update an appointment (only doctors can)
 *   - Invalid status value provided
 *   - Email already registered
 *
 * The GlobalExceptionHandler catches this and returns a 400 HTTP response.
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
