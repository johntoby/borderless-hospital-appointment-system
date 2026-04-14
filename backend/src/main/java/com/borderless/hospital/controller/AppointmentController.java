package com.borderless.hospital.controller;

import com.borderless.hospital.dto.AppointmentRequest;
import com.borderless.hospital.dto.AppointmentResponse;
import com.borderless.hospital.dto.StatusUpdateRequest;
import com.borderless.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AppointmentController — handles all appointment-related HTTP requests.
 *
 * AUTHENTICATION MECHANISM:
 *   We use a simple custom header: X-User-Id
 *   The frontend stores the logged-in user's ID and sends it with every request.
 *   The controller reads this header with @RequestHeader("X-User-Id").
 *
 *   NOTE: This is NOT secure for production. In real apps, use JWT tokens.
 *   We use this approach for teaching simplicity.
 *
 * ENDPOINTS:
 *   POST   /api/appointments           → Book a new appointment (patient)
 *   GET    /api/appointments/my        → Get patient's appointments (patient)
 *   GET    /api/appointments/doctor    → Get doctor's appointments (doctor)
 *   PUT    /api/appointments/{id}/status → Update appointment status (doctor)
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * POST /api/appointments
     *
     * A patient books a new appointment.
     * The patient's ID comes from the X-User-Id header (set by frontend after login).
     * Returns HTTP 201 Created with the new appointment.
     */
    @PostMapping
    public ResponseEntity<AppointmentResponse> bookAppointment(
            @RequestHeader("X-User-Id") Long patientId,
            @Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.bookAppointment(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/appointments/my
     *
     * A patient views their own appointments.
     * Returns a list of all appointments booked by this patient.
     */
    @GetMapping("/my")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(
            @RequestHeader("X-User-Id") Long patientId) {
        return ResponseEntity.ok(appointmentService.getPatientAppointments(patientId));
    }

    /**
     * GET /api/appointments/doctor
     *
     * A doctor views all appointments assigned to them.
     * Returns a list of all appointments where this user is the doctor.
     */
    @GetMapping("/doctor")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @RequestHeader("X-User-Id") Long doctorId) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointments(doctorId));
    }

    /**
     * PUT /api/appointments/{id}/status
     *
     * A doctor updates the status of one of their appointments.
     * @PathVariable extracts {id} from the URL.
     * Only the assigned doctor can update the status (enforced in service).
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long doctorId,
            @Valid @RequestBody StatusUpdateRequest request) {
        AppointmentResponse response = appointmentService.updateStatus(id, doctorId, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
