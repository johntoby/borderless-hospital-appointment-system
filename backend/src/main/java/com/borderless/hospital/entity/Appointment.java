package com.borderless.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Appointment Entity — maps to the "appointments" table in PostgreSQL.
 *
 * DATABASE RELATIONSHIPS:
 *   Many appointments → One patient  (Many-to-One)
 *   Many appointments → One doctor   (Many-to-One)
 *
 * WHY @ManyToOne?
 *   Think of it this way: One patient can have MANY appointments.
 *   But each appointment belongs to exactly ONE patient.
 *   So from the Appointment side, it's "Many-to-One" with User.
 *
 * @JoinColumn:
 *   Creates a foreign key column in the appointments table.
 *   "patient_id" column will hold the ID of the patient.
 *   "doctor_id" column will hold the ID of the doctor.
 *
 * @CreationTimestamp:
 *   Hibernate automatically sets this to the current timestamp
 *   when the record is first saved. We never set it manually.
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELATIONSHIP: Many appointments → One patient
    // Creates: patient_id column (foreign key → users.id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // RELATIONSHIP: Many appointments → One doctor
    // Creates: doctor_id column (foreign key → users.id)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false)
    private LocalDate appointmentDate;    // e.g., 2024-12-25

    @Column(nullable = false)
    private LocalTime appointmentTime;    // e.g., 14:30:00

    @Column(nullable = false)
    private String reason;                // e.g., "Regular checkup"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @CreationTimestamp
    private LocalDateTime createdAt;      // Auto-set by Hibernate on insert
}
