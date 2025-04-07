package com.medhead.emergency.repository;

import com.medhead.emergency.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for accessing and managing Hospital entities.
 * <p>
 * Extends JpaRepository to inherit standard CRUD operations and query methods.
 * <p>
 * - Hospital: the entity type managed by this repository.
 * - String: the type of the entity's primary key (Hospital's ID).
 * <p>
 * Spring Data JPA will automatically provide the implementation at runtime.
 */
public interface HospitalRepository extends JpaRepository<Hospital, String> {
}