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
     * Uses a single spatial+availability query, puis un batch ORS matrix pour les temps de trajet.
     */
    @Transactional(Transactional.TxType.SUPPORTS)  // lecture seule
    public Optional<Hospital> findBestHospital(double lat, double lon, int specialityId) {
        logger.info("Searching for best hospital for speciality '{}' at location ({}, {})",
                specialityId, lat, lon);

        // 1) On récupère directement les 5 hôpitaux les plus proches avec dispo
        List<Hospital> candidates = hospitalRepository
                .findNearestAvailable(lat, lon, specialityId, 5);

        if (!candidates.isEmpty()) {
            logger.info("Found {} candidate hospitals via spatial+availability index",
                    candidates.size());

            if (candidates.size() == 1) {
                return Optional.of(candidates.get(0));
            }

            // 2) Un seul appel batch pour tous les temps de trajet
            Map<String,Integer> travelTimes = travelTimeService
                    .getTravelTimesBatch(lat, lon, candidates);

            // 3) Choix du meilleur
            Hospital best = candidates.stream()
                    .min(Comparator.comparingInt(h -> travelTimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                    .orElseThrow();

            logger.info("Selected hospital: {} (ID={})", best.getName(), best.getOrgId());
            return Optional.of(best);
        }

        // Fallback #1 : étendre à 10
        logger.warn("No hospitals in top 5; falling back to top 10");
        List<Hospital> fallback = hospitalRepository
                .findNearestAvailable(lat, lon, specialityId, 10);

        if (!fallback.isEmpty()) {
            Map<String,Integer> ftimes = travelTimeService.getTravelTimesBatch(lat, lon, fallback);
            Hospital bestFb = fallback.stream()
                    .min(Comparator.comparingInt(h -> ftimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                    .orElseThrow();

            logger.info("Selected fallback hospital: {} (ID={})", bestFb.getName(), bestFb.getOrgId());
            return Optional.of(bestFb);
        }

        // Fallback #2 : scan complet
        logger.warn("No hospitals available in fallback; performing full scan");
        List<Hospital> all = hospitalRepository.findAll().stream()
                .filter(h -> h.getLatitude() != null && h.getLongitude() != null)
                .filter(h -> availabilityService.hasAvailableBedForSpeciality(h, specialityId))
                .toList();

        if (all.isEmpty()) {
            logger.error("No hospital with available beds for speciality '{}'", specialityId);
            return Optional.empty();
        }

        Map<String,Integer> allTimes = travelTimeService.getTravelTimesBatch(lat, lon, all);
        Hospital bestOverall = all.stream()
                .min(Comparator.comparingInt(h -> allTimes.getOrDefault(h.getOrgId(), Integer.MAX_VALUE)))
                .orElseThrow();

        logger.info("Selected hospital by full scan: {} (ID={})",
                bestOverall.getName(), bestOverall.getOrgId());
        return Optional.of(bestOverall);
    }
}
