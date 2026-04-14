package com.borderless.hospital.service;

import com.borderless.hospital.dto.DoctorResponse;
import com.borderless.hospital.entity.Role;
import com.borderless.hospital.entity.User;
import com.borderless.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DoctorService — business logic for doctor-related operations.
 *
 * Currently has one job: retrieve all doctors from the database
 * and convert them to DoctorResponse DTOs for the frontend.
 */
@Service
@RequiredArgsConstructor
public class DoctorService {

    private final UserRepository userRepository;

    /**
     * Get a list of all available doctors.
     *
     * Queries the DB for users with Role.DOCTOR, then
     * converts each User entity to a DoctorResponse DTO.
     *
     * The Stream API steps:
     *   .stream()         → turns the List into a stream for processing
     *   .map(doctor -> )  → transforms each User into a DoctorResponse
     *   .collect(...)     → collects results back into a List
     */
    public List<DoctorResponse> getAllDoctors() {
        List<User> doctors = userRepository.findByRole(Role.DOCTOR);

        return doctors.stream()
                .map(doctor -> new DoctorResponse(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getEmail(),
                        doctor.getSpecialty()
                ))
                .collect(Collectors.toList());
    }
}
