package com.borderless.hospital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * LoginRequest DTO (Data Transfer Object)
 *
 * WHY DTOs?
 *   We never expose Entity classes directly to the API.
 *   DTOs are plain objects used to carry data between the
 *   client (frontend) and the controller.
 *
 *   Benefits:
 *   - Control exactly what data comes IN and goes OUT
 *   - Add validation rules separate from the database model
 *   - Hide sensitive fields (e.g., password hashes)
 *
 * VALIDATION ANNOTATIONS:
 *   @NotBlank → field must not be null, empty, or whitespace
 *   @Email    → field must be a valid email format
 *   These are enforced when we use @Valid in the controller.
 */
@Data
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
