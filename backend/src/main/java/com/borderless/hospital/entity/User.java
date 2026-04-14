package com.borderless.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Entity — maps to the "users" table in PostgreSQL.
 *
 * This single table stores BOTH patients and doctors.
 * The "role" column differentiates them.
 *
 * LOMBOK ANNOTATIONS:
 *   @Data           → generates getters, setters, toString, equals, hashCode
 *   @NoArgsConstructor → generates a no-argument constructor (required by JPA)
 *   @AllArgsConstructor → generates a constructor with all fields
 *
 * JPA ANNOTATIONS:
 *   @Entity  → tells Hibernate this class maps to a database table
 *   @Table   → specifies the exact table name
 *   @Id      → marks the primary key field
 *   @GeneratedValue → auto-increment the ID
 *   @Column  → customizes the column (nullable, unique, etc.)
 *   @Enumerated → stores the enum as a readable STRING in the DB
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)   // Emails must be unique
    private String email;

    @Column(nullable = false)
    private String password;
    // NOTE: In production ALWAYS hash passwords with BCrypt or similar!
    // We use plain text here only for teaching simplicity.

    @Enumerated(EnumType.STRING)   // Stores "PATIENT" or "DOCTOR" in DB
    @Column(nullable = false)
    private Role role;

    // Only populated for doctors (null for patients)
    private String specialty;
}
