package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.serviceInterface.TravelTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

/**
 * Dummy implementation of {@link TravelTimeService} used for stress testing environments.
 *
 * <p>This service is only active when the {@code "stress"} Spring profile is enabled.
 * It is also marked as {@link Primary}, which makes it override any other {@link TravelTimeService}
 * implementations when the profile is active.</p>
 *
 * <p>Its purpose is to simulate a stable and predictable environment during high-load testing
 * by returning fixed travel times for all hospital candidates. This avoids external API calls,
 * ensuring faster and deterministic results.</p>
 *
 * <h3>Usage scenarios:</h3>
 * <ul>
 *     <li>Stress/load testing of the hospital allocation logic</li>
 *     <li>Testing behavior of fallback mechanisms without network dependency</li>
 *     <li>Running benchmarks or CI pipelines where real travel time computation isn't needed</li>
 * </ul>
 *
 * <h3>How to activate:</h3>
 * <p>Run your application or tests with the {@code stress} profile:</p>
 * <pre>{@code
 *   -Dspring.profiles.active=stress
 * }</pre>
 * <p>Or set it via environment variable:</p>
 * <pre>{@code
 *   SPRING_PROFILES_ACTIVE=stress
 * }</pre>
 */
@Service
@Profile("stress")
@Primary
public class DummyTravelTimeService implements TravelTimeService {

    private static final Logger logger = LoggerFactory.getLogger(DummyTravelTimeService.class);

    /**
     * Returns a fixed travel time (5 minutes) for all hospitals in the batch.
     *
     * @param startLat  the starting latitude
     * @param startLon  the starting longitude
     * @param candidates list of hospital candidates
     * @return a map of hospital IDs to fixed travel time (5 minutes)
     */
    @Override
    public Map<String, Integer> getTravelTimesBatch(
            double startLat, double startLon, List<Hospital> candidates) {

        logger.warn("DummyTravelTimeService - using fixed travel times (stress profile)");

        return candidates.stream()
                .collect(toMap(
                        Hospital::getOrgId,
                        h -> 5, // fixed 5-minute travel time
                        (existing, replacement) -> existing // merge strategy (if duplicate keys)
                ));
    }

    /**
     * Returns a fixed travel time of 5 minutes between any two locations.
     *
     * @param sLat starting latitude
     * @param sLon starting longitude
     * @param eLat ending latitude
     * @param eLon ending longitude
     * @return 5 (minutes)
     */
    @Override
    public int getTravelTimeInMinutes(
            double sLat, double sLon, double eLat, double eLon) {
        return 5;
    }
}
