package com.borderless.hospital.entity;

/**
 * Role enum — defines the two types of users in our system.
 *
 * Stored as a String in the database (via @Enumerated(EnumType.STRING))
 * so the DB column shows "PATIENT" or "DOCTOR" instead of 0 or 1.
 */
public enum Role {
    PATIENT,   // A person who books appointments
    DOCTOR     // A medical professional who receives appointments
}
