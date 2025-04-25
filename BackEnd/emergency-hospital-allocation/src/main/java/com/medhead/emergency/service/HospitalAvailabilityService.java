package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service responsible for checking and allocating hospital beds for a specific medical specialty.
 *
 * <p>This service provides two main functionalities:</p>
 * <ul>
 *     <li>Check if a given hospital has available beds for a particular specialty</li>
 *     <li>Attempt to allocate (decrement) a bed in the database for a given hospital and specialty</li>
 * </ul>
 *
 * <p>Both methods are transactional to ensure consistency in read and write operations related to bed availability.</p>
 */
@Service
public class HospitalAvailabilityService {

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * Checks whether the specified hospital has at least one available bed for the given specialty.
     *
     * @param hospital     the hospital entity to check
     * @param specialityId the ID of the medical specialty
     * @return true if at least one available bed exists for the given specialty, false otherwise
     */
    @Transactional
    public boolean hasAvailableBedForSpeciality(Hospital hospital, Integer specialityId) {
        return hospital.getHospitalSpecialities().stream()
                .anyMatch(hs ->
                        hs.getSpeciality().getId().equals(specialityId)
                                && hs.getAvailableBeds() > 0
                );
    }

    /**
     * Attempts to allocate (i.e. decrement) an available bed for a given hospital and specialty.
     *
     * <p>If no beds are available, an {@link IllegalStateException} is thrown.</p>
     *
     * @param hospitalId   the ID of the hospital
     * @param specialityId the ID of the medical specialty
     * @throws IllegalStateException if no available beds were found or the allocation failed
     */
    @Transactional
    public void allocateBed(String hospitalId, Integer specialityId) {
        int updated = hospitalRepository.decrementAvailableBeds(hospitalId, specialityId);

        if (updated == 0) {
            throw new IllegalStateException(
                    "No available beds for speciality id: " + specialityId
            );
        }
    }
}
