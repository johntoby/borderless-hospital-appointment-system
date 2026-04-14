package com.borderless.hospital.service;

import com.borderless.hospital.dto.AuthResponse;
import com.borderless.hospital.dto.LoginRequest;
import com.borderless.hospital.dto.RegisterRequest;
import com.borderless.hospital.entity.Role;
import com.borderless.hospital.entity.User;
import com.borderless.hospital.exception.BadRequestException;
import com.borderless.hospital.exception.ResourceNotFoundException;
import com.borderless.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AuthService — business logic for registration and login.
 *
 * LAYERED ARCHITECTURE ROLE: Service Layer
 *   - Controllers call service methods (they don't talk to the DB directly)
 *   - Services contain business rules and call repositories
 *   - Repositories talk to the database
 *
 * @Service marks this as a Spring-managed service bean.
 * @RequiredArgsConstructor (Lombok) generates a constructor for all final fields.
 *   Spring uses this constructor to inject dependencies (Dependency Injection).
 * @Slf4j (Lombok) adds a "log" variable for logging.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // Injected by Spring via constructor (see @RequiredArgsConstructor)
    private final UserRepository userRepository;

    /**
     * Register a new patient account.
     *
     * Steps:
     *   1. Check if the email is already taken → throw BadRequestException if yes
     *   2. Create a new User entity with PATIENT role
     *   3. Save to database via repository
     *   4. Return a response DTO (never the raw entity)
     */
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new patient with email: {}", request.getEmail());

        // Step 1: Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("An account with this email already exists. Please log in.");
        }

        // Step 2: Create new User entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        // NOTE: In production, ALWAYS hash passwords:
        //   user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);   // All self-registered users are patients

        // Step 3: Save to database
        User savedUser = userRepository.save(user);
        log.info("New patient registered: {} (ID: {})", savedUser.getName(), savedUser.getId());

        // Step 4: Return response DTO
        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "Registration successful! Welcome to Borderless Hospital."
        );
    }

    /**
     * Login an existing user (patient or doctor).
     *
     * Steps:
     *   1. Find user by email → 404 if not found
     *   2. Check password → 400 if wrong
     *   3. Return response with user info
     */
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for email: {}", request.getEmail());

        // Step 1: Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + request.getEmail()
                ));

        // Step 2: Verify password (plain text comparison for simplicity)
        // In production: BCryptPasswordEncoder.matches(rawPassword, hashedPassword)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Incorrect password. Please try again.");
        }

        log.info("User logged in: {} (Role: {})", user.getName(), user.getRole());

        // Step 3: Return user info to frontend
        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                "Login successful! Welcome back, " + user.getName() + "."
        );
    }
}
