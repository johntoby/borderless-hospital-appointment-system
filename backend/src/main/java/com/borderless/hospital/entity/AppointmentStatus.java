package com.borderless.hospital.entity;

/**
 * AppointmentStatus enum — the lifecycle states of an appointment.
 *
 * Flow:
 *   Patient books → SCHEDULED
 *   Doctor marks done → COMPLETED
 *   Doctor or system cancels → CANCELLED
 */
public enum AppointmentStatus {
    SCHEDULED,    // Default: appointment has been booked, awaiting visit
    COMPLETED,    // Doctor has seen the patient
    CANCELLED     // Appointment was cancelled
}
