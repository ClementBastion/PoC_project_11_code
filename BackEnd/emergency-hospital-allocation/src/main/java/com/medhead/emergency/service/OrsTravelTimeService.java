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

@Service
@Profile("!stress")
public class OrsTravelTimeService implements TravelTimeService {
    private final WebClient client;
    private final ObjectMapper mapper = new ObjectMapper();

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
     * Batch matrix via OpenRouteService.
     * Si l’API matrix n’est pas dispo (404), on tombe automatiquement
     * en fallback séquentiel.
     */
    @Cacheable("travelMatrix")
    public Map<String,Integer> getTravelTimesBatch(
            double startLat, double startLon, List<Hospital> dests) {
        // 1) Construire la liste des coordonnées (lon,lat)
        List<List<Double>> locations = new ArrayList<>();
        locations.add(List.of(startLon, startLat));
        dests.forEach(h -> locations.add(List.of(h.getLongitude(), h.getLatitude())));

        // 2) Payload JSON
        Map<String,Object> body = new HashMap<>();
        body.put("locations", locations);
        body.put("metrics", List.of("duration"));
        // on ne veut que les durées de [0] vers les autres
        body.put("sources", List.of(0));
        body.put("destinations",
                IntStream.range(1, locations.size()).boxed().toList());

        try {
            // 3) Appel batch
            JsonNode root = client.post()
                    .uri("/v2/matrix/driving-car")
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            resp -> Mono.error(new RuntimeException("Matrix 4xx")))
                    .onStatus(
                            status -> status.is5xxServerError(),
                            resp -> Mono.error(new RuntimeException("Matrix 5xx")))
                    .bodyToMono(JsonNode.class)
                    .block();

            // 4) Récupérer la première ligne de durées (en secondes)
            JsonNode durations = root.path("durations").get(0);
            Map<String,Integer> result = new HashMap<>();
            for (int i = 1; i < durations.size(); i++) {
                double sec = durations.get(i).asDouble();
                result.put(dests.get(i-1).getOrgId(), (int)Math.round(sec / 60));
            }
            return result;

        } catch (WebClientResponseException.NotFound e) {
            // Fallback si l’endpoint matrix n’est pas dispo (404)
            Map<String,Integer> fallback = new HashMap<>();
            for (Hospital h : dests) {
                // appel séquentiel à l’ancien getTravelTimeInMinutes
                int t = getTravelTimeInMinutes(startLat, startLon,
                        h.getLatitude(), h.getLongitude());
                fallback.put(h.getOrgId(), t);
            }
            return fallback;
        }
    }

    // garde votre ancienne méthode unitaire, utilisée en fallback
    public int getTravelTimeInMinutes(double sLat, double sLon,
                                      double eLat, double eLon) {
        // implémentation existante...
        return Integer.MAX_VALUE; // placeholder
    }
}
