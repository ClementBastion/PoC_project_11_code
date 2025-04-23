package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import com.medhead.emergency.serviceInterface.TravelTimeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service responsible for locating the best hospital for a patient in need of a specific medical specialty.
 *
 * <p>This service integrates hospital availability with geographic proximity and estimated travel time,
 * using a layered fallback strategy:</p>
 * <ol>
 *     <li>Attempt to find a hospital within the top 5 nearest hospitals that has availability</li>
 *     <li>Fallback to the top 10 if none are available in the top 5</li>
 *     <li>Perform a full scan of all hospitals as a last resort</li>
 * </ol>
 *
 * <p>The service also uses a batch travel time calculation to select the optimal hospital based on proximity and availability.</p>
 */
@Service
public class HospitalLocatorService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalLocatorService.class);

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private TravelTimeService travelTimeService;

    @Autowired
    private HospitalAvailabilityService availabilityService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Finds the best hospital for a given specialty and patient location.
     * Uses a spatial + availability query and a single batch call for travel time estimation.
     *
     * @param lat          Latitude of the patient
     * @param lon          Longitude of the patient
     * @param specialityId ID of the medical specialty needed
     * @return the best available hospital as an {@link Optional}, or {@link Optional#empty()} if none are found
     */
    @Transactional(Transactional.TxType.SUPPORTS) // read-only transaction
    public Optional<Hospital> findBestHospital(double lat, double lon, int specialityId) {
        logger.info("Searching for the best hospital for specialty '{}' at location ({}, {})",
                specialityId, lat, lon);

        // 1) First, try the top 5 nearest hospitals that have available beds for the given specialty
        List<Hospital> candidates = hospitalRepository
                .findNearestAvailable(lat, lon, specialityId, 5);

        if (!candidates.isEmpty()) {
            logger.info("Found {} candidate hospitals via spatial + availability index", candidates.size());

            if (candidates.size() == 1) {
                return Optional.of(candidates.get(0));
            }

            // 2) Batch call to calculate travel times to all candidates
            Map<String, Integer> travelTimes = travelTimeService.getTravelTimesBatch(lat, lon, candidates);

            // 3) Select the hospital with the shortest travel time
            Hospital best = candidates.stream()
                    .min(Comparator.comparingInt(h -> travelTimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                    .orElseThrow();

            logger.info("Selected hospital: {} (ID={})", best.getName(), best.getOrgId());
            return Optional.of(best);
        }

        // Fallback #1: Try the top 10 nearest hospitals
        logger.warn("No hospitals in top 5; falling back to top 10");
        List<Hospital> fallback = hospitalRepository
                .findNearestAvailable(lat, lon, specialityId, 10);

        if (!fallback.isEmpty()) {
            Map<String, Integer> fallbackTimes = travelTimeService.getTravelTimesBatch(lat, lon, fallback);
            Hospital bestFallback = fallback.stream()
                    .min(Comparator.comparingInt(h -> fallbackTimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                    .orElseThrow();

            logger.info("Selected fallback hospital: {} (ID={})", bestFallback.getName(), bestFallback.getOrgId());
            return Optional.of(bestFallback);
        }

        // Fallback #2: Full scan of all hospitals
        logger.warn("No hospitals available in fallback; performing full scan");
        List<Hospital> all = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, specialityId))
                .toList();

        if (all.isEmpty()) {
            logger.error("No hospital with available beds for specialty '{}'", specialityId);
            return Optional.empty();
        }

        Map<String, Integer> allTravelTimes = travelTimeService.getTravelTimesBatch(lat, lon, all);
        Hospital bestOverall = all.stream()
                .min(Comparator.comparingInt(h -> allTravelTimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                .orElseThrow();

        logger.info("Selected hospital by full scan: {} (ID={})", bestOverall.getName(), bestOverall.getOrgId());
        return Optional.of(bestOverall);
    }
}
