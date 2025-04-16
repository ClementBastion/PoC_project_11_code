package com.medhead.emergency.controller;

import com.medhead.emergency.DTO.MinHospitalDTO;
import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.service.HospitalAvailabilityService;
import com.medhead.emergency.service.HospitalLocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalLocatorController {

    @Autowired
    private HospitalLocatorService hospitalLocatorService;

    @Autowired
    private HospitalAvailabilityService availabilityService;


    /**
     * Endpoint to find the best hospital for a given patient location and medical speciality.
     * <p>
     * Example request:
     * GET /api/hospitals/search?lat=51.5074&lon=-0.1278&speciality=Cardiology
     * <p>
     * @param lat Latitude of the patient
     * @param lon Longitude of the patient
     * @param speciality Required medical speciality
     * @return The best matching hospital or 404 if none found
     */
    @GetMapping("/search")
    public ResponseEntity<?> findNearestHospital(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String speciality
    ) {
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, speciality);

        if (result.isPresent()) {
            Hospital hospital = result.get();
            availabilityService.allocateBed(hospital.getOrgId(), speciality);
        }

        return result
                .map(MinHospitalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/stress-test-search")
    public ResponseEntity<?> findNearestHospitalStressTest(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String speciality
    ) {
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, speciality);

        return result
                .map(MinHospitalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
