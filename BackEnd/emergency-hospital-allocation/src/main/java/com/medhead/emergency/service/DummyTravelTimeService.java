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

@Service
@Profile("stress")
@Primary
public class DummyTravelTimeService implements TravelTimeService {

    private static final Logger logger = LoggerFactory.getLogger(DummyTravelTimeService.class);
    @Override
    public Map<String,Integer> getTravelTimesBatch(
            double startLat, double startLon, List<Hospital> candidates) {
        logger.warn("DummyTravelTimeService - stress profile");
        return candidates.stream()
                .collect(toMap(
                        Hospital::getOrgId,
                        h -> 5,
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public int getTravelTimeInMinutes(
            double sLat, double sLon, double eLat, double eLon) {
        return 5;
    }
}
