package com.medhead.emergency.repository;

import com.medhead.emergency.entity.Speciality;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialityRepository extends JpaRepository<Speciality, Long> {
    Optional<Speciality> findByName(String specialityName);

    Optional<Speciality> findByNameIgnoreCase(String specialityName);
}
