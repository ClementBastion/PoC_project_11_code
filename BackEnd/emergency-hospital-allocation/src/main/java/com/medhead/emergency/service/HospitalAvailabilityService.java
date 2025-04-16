package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.entity.HospitalSpeciality;
import com.medhead.emergency.event.BedAllocationEventPublisher;
import com.medhead.emergency.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class HospitalAvailabilityService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private BedAllocationEventPublisher bedAllocationEventPublisher;

    @Transactional
    public boolean hasAvailableBedForSpeciality(Hospital hospital, String speciality) {
        return hospital.getHospitalSpecialities().stream()
                .anyMatch(hs -> hs.getSpeciality().getName().equalsIgnoreCase(speciality)
                        && hs.getAvailableBeds() > 0);
    }

    @Transactional
    public void allocateBed(String hospitalId, String specialityName) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("Hospital not found: " + hospitalId));

        HospitalSpeciality target = hospital.getHospitalSpecialities().stream()
                .filter(hs -> hs.getSpeciality().getName().equalsIgnoreCase(specialityName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Speciality '" + specialityName + "' not found in hospital " + hospital.getName()));

        if (target.getAvailableBeds() <= 0) {
            throw new IllegalStateException("No available beds for speciality: " + specialityName);
        }

        target.setAvailableBeds(target.getAvailableBeds() - 1);

        hospitalRepository.save(hospital);

        // Publish event
        bedAllocationEventPublisher.publishBedAllocated(hospital, specialityName);
    }
}
