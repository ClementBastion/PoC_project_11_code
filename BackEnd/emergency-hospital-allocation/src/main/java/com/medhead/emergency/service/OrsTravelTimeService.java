package com.medhead.emergency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.serviceInterface.TravelTimeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Travel time service that integrates with the OpenRouteService (ORS) Matrix API.
 *
 * <p>This implementation calculates estimated travel times between a single source and multiple
 * hospital destinations using a batch matrix call. If the matrix API is unavailable or returns a 404 error,
 * it gracefully falls back to sequential single-point estimations using {@code getTravelTimeInMinutes()}.</p>
 *
 * <p>This service is only active when the {@code "stress"} profile is NOT active.</p>
 */
@Service
@Profile("!stress")
public class OrsTravelTimeService implements TravelTimeService {

    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructs the ORS WebClient with the provided API key.
     *
     * @param apiKey the OpenRouteService API key from application properties
     */
    public OrsTravelTimeService(@Value("${ors.api.key}") String apiKey) {
        this.client = WebClient.builder()
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader(HttpHeaders.AUTHORIZATION, apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT,
                        "application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8")
                .build();
    }

    /**
     * Retrieves travel times from a single patient location to multiple hospitals using the ORS Matrix API.
     * If the API endpoint is not available (e.g. returns 404), falls back to sequential calls using the legacy method.
     *
     * <p>The results are cached under the {@code travelMatrix} cache.</p>
     *
     * @param startLat the latitude of the origin (patient)
     * @param startLon the longitude of the origin (patient)
     * @param dests    the list of hospital destinations
     * @return a map from hospital ID to travel time in minutes
     */
    @Cacheable("travelMatrix")
    public Map<String, Integer> getTravelTimesBatch(
            double startLat, double startLon, List<Hospital> dests) {

        // 1) Build the list of coordinates [lon, lat] with source first
        List<List<Double>> locations = new ArrayList<>();
        locations.add(List.of(startLon, startLat));
        dests.forEach(h -> locations.add(List.of(h.getLongitude(), h.getLatitude())));

        // 2) Create the request payload
        Map<String, Object> body = new HashMap<>();
        body.put("locations", locations);
        body.put("metrics", List.of("duration"));
        body.put("sources", List.of(0));
        body.put("destinations", IntStream.range(1, locations.size()).boxed().toList());

        try {
            // 3) Make the ORS matrix API call
            JsonNode root = client.post()
                    .uri("/v2/matrix/driving-car")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            resp -> Mono.error(new RuntimeException("Matrix 4xx "+resp.toString())))
                    .onStatus(status -> status.is5xxServerError(),
                            resp -> Mono.error(new RuntimeException("Matrix 5xx")))
                    .bodyToMono(JsonNode.class)
                    .block();

            // 4) Parse the duration matrix (in seconds) and convert to minutes
            JsonNode durations = root.path("durations").get(0);
            Map<String, Integer> result = new HashMap<>();
            for (int i = 1; i < durations.size(); i++) {
                double sec = durations.get(i).asDouble();
                result.put(dests.get(i - 1).getOrgId(), (int) Math.round(sec / 60));
            }
            return result;

        } catch (WebClientResponseException.NotFound e) {
            // Fallback if matrix endpoint is not available (404)
            Map<String, Integer> fallback = new HashMap<>();
            for (Hospital h : dests) {
                int t = getTravelTimeInMinutes(startLat, startLon, h.getLatitude(), h.getLongitude());
                fallback.put(h.getOrgId(), t);
            }
            return fallback;
        }
    }

    /**
     * Fallback method for computing travel time between two coordinates.
     * Currently acts as a placeholder returning {@link Integer#MAX_VALUE}.
     *
     * @param sLat source latitude
     * @param sLon source longitude
     * @param eLat destination latitude
     * @param eLon destination longitude
     * @return estimated travel time in minutes (placeholder)
     */
    @Override
    public int getTravelTimeInMinutes(double sLat, double sLon, double eLat, double eLon) {
        try {
            // Build the request payload
            Map<String, Object> body = new HashMap<>();
            body.put("coordinates", List.of(
                    List.of(sLon, sLat),  // Start point: [lon, lat]
                    List.of(eLon, eLat)   // End point: [lon, lat]
            ));

            // Call the ORS directions API
            JsonNode root = client.post()
                    .uri("/v2/directions/driving-car")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(status -> status.isError(),
                            resp -> Mono.error(new RuntimeException("ORS directions error")))
                    .bodyToMono(JsonNode.class)
                    .block();

            // Extract duration in seconds and convert to minutes
            double durationInSeconds = root.path("routes").get(0)
                    .path("summary").path("duration").asDouble();

            return (int) Math.round(durationInSeconds / 60);

        } catch (Exception e) {
            // Fallback failed — return max to deprioritize this hospital
            return Integer.MAX_VALUE;
        }
    }
}
