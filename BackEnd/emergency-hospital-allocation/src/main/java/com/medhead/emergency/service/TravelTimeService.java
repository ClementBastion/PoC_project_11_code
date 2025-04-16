package com.medhead.emergency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TravelTimeService {

    // API key for OpenRouteService
    @Value("${ors.api.key}")
    private String apiKey;

    @Autowired
    private Environment env;

    /**
     * Calculates estimated driving time in minutes between two coordinates using OpenRouteService API.
     *
     * @param startLat Starting latitude
     * @param startLon Starting longitude
     * @param endLat Destination latitude
     * @param endLon Destination longitude
     * @return Estimated travel time in minutes, or Integer.MAX_VALUE if error occurs
     */
    @Cacheable("travelTimes")
    public int getTravelTimeInMinutes(double startLat, double startLon, double endLat, double endLon) {
        try {
            // Validate input coordinates to avoid invalid API calls
            if (Double.isNaN(startLat) || Double.isNaN(startLon) || Double.isNaN(endLat) || Double.isNaN(endLon)) {
                System.err.println("❌ Invalid coordinates: NaN");
                return Integer.MAX_VALUE;
            }

            // Construct API URL
            String url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + apiKey;

            // Prepare JSON payload with coordinates, ensuring decimal points (Locale.US)
            String payload = String.format(
                    Locale.US,
                    "{\"coordinates\":[[%f,%f],[%f,%f]]}",
                    startLon, startLat, endLon, endLat
            );
            System.out.println("Payload ORS: " + payload);

            // Create and configure HTTP connection
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            // Send request payload
            conn.getOutputStream().write(payload.getBytes(StandardCharsets.UTF_8));

            // Read the response from API
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.lines().collect(Collectors.joining("\n"));

            // Parse the JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode routes = root.path("routes");

            // Check that at least one route was returned
            if (!routes.isArray() || routes.isEmpty()) {
                System.err.println("❌ No route found in ORS response: " + response);
                return Integer.MAX_VALUE;
            }

            // Extract travel time from the first route's summary (in seconds)
            JsonNode summary = routes.get(0).path("summary");
            double duration = summary.path("duration").asDouble(); // duration in seconds
            System.err.println("duration : " + duration / 60);

            // Convert duration to minutes and return
            return (int) Math.round(duration / 60);

        } catch (Exception e) {
            // Handle any exception (network, parsing, etc.)
            System.err.println(e.getMessage());
            return Integer.MAX_VALUE;
        }
    }
}
