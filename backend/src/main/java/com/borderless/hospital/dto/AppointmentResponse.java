package com.borderless.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * AppointmentResponse DTO — returned when querying appointments.
 *
 * Notice we "flatten" the nested User objects (patient, doctor) into
 * simple fields like patientName and doctorName. This makes it
 * easier for the frontend to display without extra API calls.
 *
 * This is sometimes called "flattening" or "projection".
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponse {
    private Long id;

    // Patient info (flattened from User entity)
    private Long patientId;
    private String patientName;

    // Doctor info (flattened from User entity)
    private Long doctorId;
    private String doctorName;
    private String doctorSpecialty;

    // Appointment details
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String reason;
    private String status;          // "SCHEDULED", "COMPLETED", or "CANCELLED"

    private LocalDateTime createdAt;
}
