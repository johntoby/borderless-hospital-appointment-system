package com.borderless.hospital.config;

import com.borderless.hospital.entity.Role;
import com.borderless.hospital.entity.User;
import com.borderless.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * DataSeeder — automatically seeds the database with doctor data on startup.
 *
 * HOW IT WORKS:
 *   Implements CommandLineRunner, which has a run() method that Spring Boot
 *   calls automatically AFTER the application context is fully loaded.
 *   This means the database tables already exist when run() executes.
 *
 * WHEN DOES IT SEED?
 *   It checks if any doctors exist first. If yes, it skips seeding.
 *   This means it only seeds once — on the very first startup.
 *   Subsequent restarts skip seeding (data already exists).
 *
 * WHY PRE-SEED DOCTORS?
 *   - In a real hospital, doctors are registered by admins, not self-registered.
 *   - This gives us test data without manually inserting SQL.
 *   - Students can log in as doctors immediately.
 *
 * DOCTOR LOGIN CREDENTIALS (for testing):
 *   Email: sarah.johnson@borderlesshospital.com  | Password: doctor123
 *   Email: michael.chen@borderlesshospital.com   | Password: doctor123
 *   (see full list below)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // Only seed if no doctors exist in the database yet
        if (userRepository.findByRole(Role.DOCTOR).isEmpty()) {
            log.info("=== Seeding doctor data into database... ===");
            seedDoctors();
            log.info("=== Doctor seeding complete! {} doctors added. ===",
                    userRepository.findByRole(Role.DOCTOR).size());
        } else {
            log.info("=== Doctors already exist in database. Skipping seed. ===");
        }
    }

    private void seedDoctors() {
        List<User> doctors = Arrays.asList(
                createDoctor("Dr. Sarah Johnson",    "sarah.johnson@borderlesshospital.com",    "Cardiologist"),
                createDoctor("Dr. Michael Chen",     "michael.chen@borderlesshospital.com",     "Neurologist"),
                createDoctor("Dr. Emily Rodriguez",  "emily.rodriguez@borderlesshospital.com",  "Pediatrician"),
                createDoctor("Dr. David Okonkwo",    "david.okonkwo@borderlesshospital.com",    "Orthopedic Surgeon"),
                createDoctor("Dr. Fatima Al-Rashid", "fatima.alrashid@borderlesshospital.com",  "Dermatologist"),
                createDoctor("Dr. James Patel",      "james.patel@borderlesshospital.com",      "General Practitioner")
        );

        userRepository.saveAll(doctors);
    }

    /**
     * Helper to build a Doctor User object.
     * All seeded doctors share the password "doctor123" for easy testing.
     */
    private User createDoctor(String name, String email, String specialty) {
        User doctor = new User();
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPassword("doctor123");   // Plain text for teaching only
        doctor.setRole(Role.DOCTOR);
        doctor.setSpecialty(specialty);
        return doctor;
    }
}
