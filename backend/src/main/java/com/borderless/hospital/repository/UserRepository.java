package com.borderless.hospital.repository;

import com.borderless.hospital.entity.Role;
import com.borderless.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserRepository — the Data Access Layer for the User entity.
 *
 * HOW IT WORKS:
 *   By extending JpaRepository<User, Long>, Spring Data JPA automatically
 *   generates the SQL for common operations like save(), findById(),
 *   findAll(), deleteById(), etc.
 *
 *   We only need to DECLARE method signatures for custom queries.
 *   Spring Data reads the method name and generates the SQL automatically!
 *
 *   Example:
 *     findByEmail(String email)
 *     → SELECT * FROM users WHERE email = ?
 *
 *     findByRole(Role role)
 *     → SELECT * FROM users WHERE role = ?
 *
 *     existsByEmail(String email)
 *     → SELECT COUNT(*) > 0 FROM users WHERE email = ?
 *
 * JpaRepository<User, Long>:
 *   - User → the entity type this repository manages
 *   - Long → the type of the entity's primary key
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by their email address (for login)
    Optional<User> findByEmail(String email);

    // Check if an email is already registered (for registration validation)
    boolean existsByEmail(String email);

    // Find all users with a specific role (used to list all doctors)
    List<User> findByRole(Role role);
}
