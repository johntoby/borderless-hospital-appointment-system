package com.borderless.hospital.service;

import com.borderless.hospital.dto.AppointmentRequest;
import com.borderless.hospital.dto.AppointmentResponse;
import com.borderless.hospital.entity.Appointment;
import com.borderless.hospital.entity.AppointmentStatus;
import com.borderless.hospital.entity.Role;
import com.borderless.hospital.entity.User;
import com.borderless.hospital.exception.BadRequestException;
import com.borderless.hospital.exception.ResourceNotFoundException;
import com.borderless.hospital.repository.AppointmentRepository;
import com.borderless.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AppointmentService — all business logic for appointment management.
 *
 * Responsibilities:
 *   1. Book a new appointment (patient action)
 *   2. Get appointments for a specific patient
 *   3. Get appointments for a specific doctor
 *   4. Update appointment status (doctor action)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    /**
     * Book a new appointment.
     *
     * Business rules enforced:
     *   - The user booking must be a PATIENT
     *   - The target user must be a DOCTOR
     */
    public AppointmentResponse bookAppointment(Long patientId, AppointmentRequest request) {
        log.debug("Patient {} booking appointment with doctor {}", patientId, request.getDoctorId());

        // Fetch and validate the patient
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        if (patient.getRole() != Role.PATIENT) {
            throw new BadRequestException("Only patients can book appointments.");
        }

        // Fetch and validate the doctor
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new BadRequestException("The selected user is not a registered doctor.");
        }

        // Create the appointment entity
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setReason(request.getReason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        // Save to database
        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment #{} booked: {} with {}", saved.getId(), patient.getName(), doctor.getName());

        return convertToResponse(saved);
    }

    /**
     * Get all appointments for the logged-in patient.
     */
    public List<AppointmentResponse> getPatientAppointments(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        return appointmentRepository.findByPatient(patient)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all appointments assigned to the logged-in doctor.
     */
    public List<AppointmentResponse> getDoctorAppointments(Long doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        return appointmentRepository.findByDoctor(doctor)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update an appointment's status.
     *
     * Business rules enforced:
     *   - Only the assigned doctor can update this appointment
     *   - Status must be a valid AppointmentStatus value
     */
    public AppointmentResponse updateStatus(Long appointmentId, Long doctorId, String newStatus) {
        // Find the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        // Authorization check: only the assigned doctor can update this
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new BadRequestException("You are not authorized to update this appointment.");
        }

        // Parse and validate the status string
        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid status '" + newStatus + "'. Allowed values: SCHEDULED, COMPLETED, CANCELLED"
            );
        }

        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment #{} status updated to {}", appointmentId, status);

        return convertToResponse(updated);
    }

    /**
     * Helper: Convert an Appointment entity to an AppointmentResponse DTO.
     *
     * This "flattens" the nested patient and doctor User objects
     * so the frontend receives a simple, flat JSON object.
     */
    private AppointmentResponse convertToResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                appointment.getPatient().getName(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getName(),
                appointment.getDoctor().getSpecialty(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReason(),
                appointment.getStatus().name(),
                appointment.getCreatedAt()
        );
    }
}
