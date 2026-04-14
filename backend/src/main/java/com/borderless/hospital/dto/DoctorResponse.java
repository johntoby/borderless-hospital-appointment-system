package com.borderless.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DoctorResponse DTO — returned when listing available doctors.
 *
 * We use this instead of the User entity to:
 *   - Exclude the password field from the response
 *   - Include only the fields relevant to the doctor listing page
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorResponse {
    private Long id;
    private String name;
    private String email;
    private String specialty;    // e.g., "Cardiologist", "Pediatrician"
}
