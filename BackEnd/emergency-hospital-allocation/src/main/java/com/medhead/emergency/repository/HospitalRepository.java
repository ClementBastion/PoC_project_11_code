package com.medhead.emergency.repository;

import com.medhead.emergency.entity.Hospital;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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
    @Cacheable("allHospitals")
    List<Hospital> findAll();

//    @Cacheable("findAllWithSpecialities")
//    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.hospitalSpecialities")
//    List<Hospital> findAllWithSpecialities();

    @Query("SELECT h FROM Hospital h LEFT JOIN FETCH h.hospitalSpecialities hs LEFT JOIN FETCH hs.speciality WHERE h.orgId = :id")
    Optional<Hospital> findByIdWithSpecialities(String id);

    @Query("SELECT DISTINCT h FROM Hospital h LEFT JOIN FETCH h.hospitalSpecialities hs LEFT JOIN FETCH hs.speciality")
    List<Hospital> findAllWithSpecialities();

}