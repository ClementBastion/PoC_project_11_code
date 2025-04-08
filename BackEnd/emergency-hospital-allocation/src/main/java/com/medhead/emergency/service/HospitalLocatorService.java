package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HospitalLocatorService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private TravelTimeService travelTimeService; // Wrapper for an external routing API like OpenRouteService

    /**
     * Finds the best hospital based on speciality, availability, and proximity.
     *
     * @param lat Latitude of the patient
     * @param lon Longitude of the patient
     * @param speciality Required medical speciality
     * @return The best matching hospital, wrapped in Optional
     */
    public Optional<Hospital> findBestHospital(double lat, double lon, String speciality) {
        // 1. Retrieve all hospitals with valid coordinates and available beds for the given speciality
        List<Hospital> filteredHospitals = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .filter(h -> h.hasAvailableBedForSpeciality(speciality))
                .toList();

        // Return empty if no hospital matches the criteria
        if (filteredHospitals.isEmpty()) {
            return Optional.empty();
        }

        // 2. Sort the filtered hospitals by haversine (geographic) distance and keep the 4 closest
        List<Hospital> nearest = filteredHospitals.stream()
                .sorted(Comparator.comparingDouble(h ->
                        haversine(lat, lon, h.getLatitude(), h.getLongitude())))
                .limit(4)
                .toList();

        // 3. From the 4 closest, find the one with the shortest real-world travel time (e.g., by car)
        return nearest.stream()
                .min(Comparator.comparingInt(h ->
                        travelTimeService.getTravelTimeInMinutes(lat, lon, h.getLatitude(), h.getLongitude())));
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
}
