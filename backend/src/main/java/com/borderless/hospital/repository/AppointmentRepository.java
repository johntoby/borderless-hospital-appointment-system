package com.borderless.hospital.repository;

import com.borderless.hospital.entity.Appointment;
import com.borderless.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AppointmentRepository — the Data Access Layer for Appointment entities.
 *
 * Spring Data JPA translates these method names into SQL queries:
 *
 *   findByPatient(User patient)
 *   → SELECT * FROM appointments WHERE patient_id = ?
 *
 *   findByDoctor(User doctor)
 *   → SELECT * FROM appointments WHERE doctor_id = ?
 *
 * We pass the entire User object (not just the ID) because JPA
 * understands the relationship — it extracts the ID internally.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Get all appointments booked by a specific patient
    List<Appointment> findByPatient(User patient);

    // Get all appointments assigned to a specific doctor
    List<Appointment> findByDoctor(User doctor);
}
