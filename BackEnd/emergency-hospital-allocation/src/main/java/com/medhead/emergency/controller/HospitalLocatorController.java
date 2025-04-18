package com.medhead.emergency.controller;

import com.medhead.emergency.DTO.MinHospitalDTO;
import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.event.BedAllocationEventPublisher;
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
    private BedAllocationEventPublisher bedAllocationEventPublisher;


    /**
     * Endpoint to find the best hospital for a given patient location and medical speciality.
     * <p>
     * Example request:
     * GET /api/hospitals/search?lat=51.5074&lon=-0.1278&speciality=Cardiology
     * <p>
     * @param lat Latitude of the patient
     * @param lon Longitude of the patient
     * @param specialityId Required medical speciality
     * @return The best matching hospital or 404 if none found
     */
    @GetMapping("/search")
    public ResponseEntity<?> findNearestHospital(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam int specialityId
    ) {
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        if (result.isPresent()) {
            Hospital hospital = result.get();
            // Publish event
            bedAllocationEventPublisher.publishBedAllocated(hospital, specialityId);
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
            @RequestParam int specialityId
    ) {
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        return result
                .map(MinHospitalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
