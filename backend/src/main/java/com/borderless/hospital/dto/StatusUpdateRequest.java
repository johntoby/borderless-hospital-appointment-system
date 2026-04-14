package com.borderless.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * StatusUpdateRequest DTO — used by doctors to update appointment status.
 *
 * Accepted values: "SCHEDULED", "COMPLETED", "CANCELLED"
 * The service layer validates that the value is a valid AppointmentStatus enum.
 */
@Data
public class StatusUpdateRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
