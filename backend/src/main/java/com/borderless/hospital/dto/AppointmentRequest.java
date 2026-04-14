package com.borderless.hospital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * AppointmentRequest DTO — the data a patient sends to book an appointment.
 *
 * The patient ID is NOT included here — it comes from the X-User-Id header
 * so patients can only book appointments for themselves.
 */
@Data
public class AppointmentRequest {

    @NotNull(message = "Please select a doctor")
    private Long doctorId;

    @NotNull(message = "Please select an appointment date")
    private LocalDate appointmentDate;    // Format: "2024-12-25"

    @NotNull(message = "Please select an appointment time")
    private LocalTime appointmentTime;    // Format: "14:30:00"

    @NotBlank(message = "Please describe your reason for the visit")
    private String reason;
}
