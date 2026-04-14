package com.borderless.hospital.controller;

import com.borderless.hospital.dto.DoctorResponse;
import com.borderless.hospital.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DoctorController — handles requests for the doctor listing.
 *
 * ENDPOINTS:
 *   GET /api/doctors  → Returns a list of all available doctors
 *
 * This is a public endpoint — no authentication required.
 * Any patient (or even unauthenticated user) can see the doctor list.
 */
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * GET /api/doctors
     *
     * Returns all doctors as a JSON array.
     * The frontend uses this to populate the "Choose a Doctor" grid.
     */
    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
}
