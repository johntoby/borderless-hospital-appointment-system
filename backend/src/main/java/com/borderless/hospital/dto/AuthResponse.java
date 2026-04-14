package com.borderless.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AuthResponse DTO — returned to the frontend after login or registration.
 *
 * The frontend stores this in localStorage so it knows:
 *   - Who the user is (id, name, email)
 *   - What they can do (role: PATIENT or DOCTOR)
 *
 * NOTE: We intentionally do NOT include the password in this response.
 * The frontend uses "id" as a simple token by sending it as X-User-Id header.
 * In production, you would use JWT tokens for security.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private String role;       // "PATIENT" or "DOCTOR"
    private String message;    // e.g., "Login successful!"
}
