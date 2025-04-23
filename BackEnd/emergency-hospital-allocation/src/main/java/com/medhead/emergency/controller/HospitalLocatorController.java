package com.medhead.emergency.controller;

import com.medhead.emergency.DTO.MinHospitalDTO;
import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.event.BedAllocationEventPublisher;
import com.medhead.emergency.service.HospitalLocatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST controller that exposes endpoints for locating hospitals based on patient location
 * and medical speciality. Provides both a protected API endpoint (which publishes an event
 * when a bed is allocated) and a public endpoint intended for stress testing.
 */
@RestController
public class HospitalLocatorController {

    @Autowired
    private HospitalLocatorService hospitalLocatorService;
    // Service that contains the core logic to find the best hospital

    @Autowired
    private BedAllocationEventPublisher bedAllocationEventPublisher;
    // Publisher that broadcasts bed allocation events internally and via Kafka

    @Value("${stress-test.api-key}")
    private String stressTestApiKey;
    // API key from configuration used to secure the public stress-test endpoint

    /**
     * API endpoint: finds the best hospital, publishes a bed allocation event,
     * and returns a minimal DTO representation.
     *
     * Example:
     *   GET /api/hospitals/search?lat=51.5074&lon=-0.1278&specialityId=1
     *
     * @param lat           the patient’s latitude
     * @param lon           the patient’s longitude
     * @param specialityId  the ID of the required medical speciality
     * @return              HTTP 200 with MinHospitalDTO if found, or 404 if none found
     */
    @GetMapping("/api/hospitals/search")
    public ResponseEntity<MinHospitalDTO> findNearestHospital(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam int specialityId
    ) {
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        if (result.isPresent()) {
            // Only publish an allocation event on the protected API
            bedAllocationEventPublisher.publishBedAllocated(result.get(), specialityId);
            return ResponseEntity.ok(MinHospitalDTO.fromEntity(result.get()));
        }

        // No matching hospital found
        return ResponseEntity.notFound().build();
    }

    /**
     * Public endpoint for stress testing: finds the best hospital without publishing events.
     * Requires a valid API key in the X-API-KEY header.
     * <p>
     * Example:
     *   GET /public/hospitals/stress-test-search?lat=51.5074&lon=-0.1278&specialityId=1
     *   Header: X-API-KEY: <your-stress-test-key>
     *
     * @param apiKey        the API key passed in the header (X-API-KEY)
     * @param lat           the patient’s latitude
     * @param lon           the patient’s longitude
     * @param specialityId  the ID of the required medical speciality
     * @return              HTTP 200 with MinHospitalDTO if found, 401 if API key invalid,
     *                      or 404 if no hospital found
     */
    @GetMapping("/public/hospitals/stress-test-search")
    public ResponseEntity<MinHospitalDTO> findNearestHospitalStressTest(
            @RequestHeader(name = "X-API-KEY", required = false) String apiKey,
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam int specialityId
    ) {
        // Verify the provided API key
        if (apiKey == null || !apiKey.equals(stressTestApiKey)) {
            // Return 401 Unauthorized if the key is missing or invalid
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Perform the normal hospital lookup without publishing an event
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        return result
                .map(MinHospitalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
