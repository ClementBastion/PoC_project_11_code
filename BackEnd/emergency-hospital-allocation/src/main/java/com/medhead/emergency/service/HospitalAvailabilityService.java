package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalAvailabilityService {

    @Autowired
    private HospitalRepository hospitalRepository;



    @Transactional
    public boolean hasAvailableBedForSpeciality(Hospital hospital, Integer specialityId) {
        return hospital.getHospitalSpecialities().stream()
                .anyMatch(hs ->
                        hs.getSpeciality().getId().equals(specialityId)
                                && hs.getAvailableBeds() > 0
                );
    }

    @Transactional
    public void allocateBed(String hospitalId, Integer specialityId) {

        int updated = hospitalRepository.decrementAvailableBeds(hospitalId,specialityId);

        if (updated == 0) {
            throw new IllegalStateException(
                    "No available beds for speciality id: " + specialityId
            );
        }
    }
}
