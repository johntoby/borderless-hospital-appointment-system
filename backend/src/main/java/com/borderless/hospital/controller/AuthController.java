package com.borderless.hospital.controller;

import com.borderless.hospital.dto.AuthResponse;
import com.borderless.hospital.dto.LoginRequest;
import com.borderless.hospital.dto.RegisterRequest;
import com.borderless.hospital.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — handles HTTP requests for authentication.
 *
 * LAYERED ARCHITECTURE ROLE: Controller (Presentation Layer on the backend)
 *   - Receives HTTP requests
 *   - Validates input (@Valid)
 *   - Delegates to the service layer
 *   - Returns HTTP responses
 *
 * Controllers should NOT contain business logic — that belongs in services.
 *
 * ENDPOINTS:
 *   POST /api/auth/register  → Register a new patient
 *   POST /api/auth/login     → Login an existing user
 *
 * @RestController = @Controller + @ResponseBody
 *   Every method automatically serializes return values to JSON.
 *
 * @RequestMapping("/api/auth")
 *   All methods in this class share this base path.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * @Valid triggers validation on RegisterRequest fields.
     * If validation fails, GlobalExceptionHandler catches it automatically.
     * Returns HTTP 201 Created on success.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     *
     * Returns HTTP 200 OK with user info on success.
     * Returns 404 if email not found, 400 if password is wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
