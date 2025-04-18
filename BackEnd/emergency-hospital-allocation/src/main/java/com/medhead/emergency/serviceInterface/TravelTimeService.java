package com.medhead.emergency.serviceInterface;

import com.medhead.emergency.entity.Hospital;
import java.util.List;
import java.util.Map;

public interface TravelTimeService {
    Map<String,Integer> getTravelTimesBatch(double startLat, double startLon, List<Hospital> dests);
    int getTravelTimeInMinutes(double startLat, double startLon, double endLat, double endLon);
}
