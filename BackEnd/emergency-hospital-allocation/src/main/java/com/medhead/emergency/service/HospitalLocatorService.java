package com.medhead.emergency.service;

import com.medhead.emergency.DTO.HospitalSnapshot;
import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;

import java.util.*;

@Service
public class HospitalLocatorService {
    private static final Logger logger = LoggerFactory.getLogger(HospitalLocatorService.class);

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private TravelTimeService travelTimeService; // Wrapper for an external routing API like OpenRouteService

    @Autowired
    private HospitalAvailabilityService availabilityService;

    @Autowired
    private ModelMapper modelMapper;


    /**
     * Finds the best hospital based on speciality, availability, and proximity.
     *
     * @param lat Latitude of the patient
     * @param lon Longitude of the patient
     * @param speciality Required medical speciality
     * @return The best matching hospital, wrapped in Optional
     */
    @Transactional
    public Optional<Hospital> findBestHospital(double lat, double lon, String speciality) {

        logger.info("Searching for best hospital for speciality '{}' at location ({}, {})", speciality, lat, lon);

        // 1. Load snapshots (cached, light version)
        List<HospitalSnapshot> snapshots = getAllHospitalSnapshots();

        if (snapshots.isEmpty()) {
            logger.warn("No hospital snapshots found.");
            return Optional.empty();
        }
        logger.info("Loaded {} hospital snapshots, {} after filtering by coordinates",
                snapshots.size(),
                snapshots.stream().filter(h -> h.getLatitude() != null && h.getLongitude() != null).count());

        // 2. Keep 10 geographically closest hospitals based on snapshot
        List<HospitalSnapshot> closestSnapshots = snapshots.stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .sorted(Comparator.comparingDouble(h ->
                        haversine(lat, lon, h.getLatitude(), h.getLongitude())))
                .limit(10)
                .toList();

        logger.info("Loaded {} hospital snapshots, {} after filtering by coordinates",
                closestSnapshots.size(),
                closestSnapshots.stream().filter(h -> h.getLatitude() != null && h.getLongitude() != null).count());

        logger.debug("Selected top 10 closest hospital snapshots.");

        // 3. Load full hospital entity for each and filter by availability
        List<Hospital> availableNearby = closestSnapshots.stream()
                .map(snap -> hospitalRepository.findByIdWithSpecialities(snap.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, speciality))
                .toList();

        logger.info("Loaded {} hospital snapshots, {} after filtering by coordinates",
                availableNearby.size(),
                availableNearby.stream().filter(h -> h.getLatitude() != null && h.getLongitude() != null).count());


        if (!availableNearby.isEmpty()) {
            logger.info("Found {} nearby hospitals with available beds.", availableNearby.size());
            Hospital selected = availableNearby.stream()
                    .min(Comparator.comparingInt(h ->
                            travelTimeService.getTravelTimeInMinutes(lat, lon, h.getLatitude(), h.getLongitude())))
                    .orElseThrow();
            logger.info("Selected hospital: {}", selected.getName());
            return Optional.of(selected);
        }


        logger.warn("No beds available in top 10. Fallback to full search.");

        List<HospitalSnapshot> safeClosestSnapshots = snapshots.stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .sorted(Comparator.comparingDouble(h ->
                        haversine(lat, lon, h.getLatitude(), h.getLongitude())))
                .limit(25)
                .toList();

        List<Hospital> safeAvailableNearby = safeClosestSnapshots.stream()
                .map(snap -> hospitalRepository.findById(snap.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, speciality))
                .toList();

        if (!safeAvailableNearby.isEmpty()) {
            logger.info("Found {} nearby hospitals with available beds.", safeAvailableNearby.size());
            Hospital selected = safeAvailableNearby.stream()
                    .min(Comparator.comparingInt(h ->
                            travelTimeService.getTravelTimeInMinutes(lat, lon, h.getLatitude(), h.getLongitude())))
                    .orElseThrow();
            logger.info("Selected hospital: {}", selected.getName());
            return Optional.of(selected);
        }

        logger.warn("No beds available in top 15. Fallback to full search.");

        // 4. Fallback: reload all entities for full search
        List<Hospital> allHospitals = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, speciality))
                .toList();

        if (allHospitals.isEmpty()) {
            logger.error("No hospital with available beds for speciality '{}'", speciality);
            return Optional.empty();
        }

        Hospital selected = allHospitals.stream()
                .min(Comparator.comparingInt(h ->
                        travelTimeService.getTravelTimeInMinutes(lat, lon, h.getLatitude(), h.getLongitude())))
                .orElseThrow();

        logger.info("Selected fallback hospital: {}", selected.getName());
        return Optional.of(selected);
    }


    /**
     * Calculates the great-circle distance between two points on Earth using the Haversine formula.
     *
     * @param lat1 Latitude of the first point
     * @param lon1 Longitude of the first point
     * @param lat2 Latitude of the second point
     * @param lon2 Longitude of the second point
     * @return Distance in kilometers
     */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of Earth in kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    @Cacheable("hospitalSnapshots")
    public List<HospitalSnapshot> getAllHospitalSnapshots() {
        List<Hospital> hospitalsWithCoordinates = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .toList();

        return hospitalsWithCoordinates.stream()
                .map(h -> modelMapper.map(h, HospitalSnapshot.class))
                .toList();
    }
}
