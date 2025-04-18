package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
     * Find the best hospital for the given speciality and patient location.
     * First, delegate to the database the spatial filtering (using a GiST index
     * on the `geom` column) to get the nearest candidates with available beds.
     * Then, among those, pick the one with the lowest travel time.
     *
     * @param lat        patient latitude
     * @param lon        patient longitude
     * @param specialityId required medical speciality id
     * @return Optional containing the selected Hospital, or empty if none found
     */
    @Transactional
    public Optional<Hospital> findBestHospital(double lat, double lon, int specialityId) {
        logger.info("Searching for best hospital for speciality '{}' at location ({}, {})",
                specialityId, lat, lon);

        /* 1) Query the 5 nearest hospitals that have at least one bed for the speciality */
        List<String> nearestIds = hospitalRepository.findNearest(
                lat, lon, 5);

        if (!nearestIds.isEmpty()) {
            logger.info("Found {} candidate hospitals via spatial index", nearestIds.size());

            // 2) Load the Hospital entities in a single batch
            List<Hospital> candidates = hospitalRepository.findAvailableByIdsAndSpecialityId(nearestIds,specialityId);
            logger.info("Found {} candidate hospitals via spatial index with available bed", candidates.size());

            if (candidates.size() == 1) {
                return Optional.of(candidates.iterator().next());
            }
            // 3) Among the nearest candidates, pick the one with the lowest travel time
            Hospital best = candidates.stream()
                    .min(Comparator.comparingInt(h ->
                            travelTimeService.getTravelTimeInMinutes(
                                    lat, lon, h.getLatitude(), h.getLongitude())))
                    .orElseThrow(); // should not happen, list non-empty

            logger.info("Selected hospital: {} (ID={})", best.getName(), best.getOrgId());
            return Optional.of(best);
        }

        logger.warn("No hospitals found in top 5 spatial query; falling back to top 10");

        // Fallback #1: expand to the 10 nearest
        List<String> fallbackIds = hospitalRepository.findNearest(
                lat, lon, 10);

        if (!fallbackIds.isEmpty()) {
            List<Hospital> fallbackCandidates = hospitalRepository.findAvailableByIdsAndSpecialityId(fallbackIds,specialityId);
            logger.info("Found {} candidate hospitals via spatial index with available bed", fallbackCandidates.size());
            Hospital bestFallback = fallbackCandidates.stream()
                    .min(Comparator.comparingInt(h ->
                            travelTimeService.getTravelTimeInMinutes(
                                    lat, lon, h.getLatitude(), h.getLongitude())))
                    .orElseThrow();


            logger.info("Selected fallback hospital: {} (ID={})",
                    bestFallback.getName(), bestFallback.getOrgId());
            return Optional.of(bestFallback);
        }

        logger.warn("No hospitals available in fallback spatial query; performing full scan");

        // Fallback #2: full scan (inefficient)
        List<Hospital> allAvailable = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, specialityId))
                .toList();

        if (allAvailable.isEmpty()) {
            logger.error("No hospital with available beds for speciality '{}'", specialityId);
            return Optional.empty();
        }

        Hospital bestOverall = allAvailable.stream()
                .min(Comparator.comparingInt(h ->
                        travelTimeService.getTravelTimeInMinutes(
                                lat, lon, h.getLatitude(), h.getLongitude())))
                .orElseThrow();

        logger.info("Selected hospital by full scan: {} (ID={})",
                bestOverall.getName(), bestOverall.getOrgId());
        return Optional.of(bestOverall);
    }
}
