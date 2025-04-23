package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.repository.HospitalRepository;
import com.medhead.emergency.serviceInterface.TravelTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalLocatorServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private TravelTimeService travelTimeService;

    @Mock
    private HospitalAvailabilityService availabilityService;

    @InjectMocks
    private HospitalLocatorService hospitalLocatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        injectField("modelMapper", new ModelMapper());
    }

    private void injectField(String fieldName, Object value) {
        try {
            var field = HospitalLocatorService.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(hospitalLocatorService, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Hospital hospital(String id, String name, double lat, double lon) {
        Hospital h = new Hospital();
        h.setOrgId(id);
        h.setName(name);
        h.setLatitude(lat);
        h.setLongitude(lon);
        return h;
    }

    @Test
    void shouldReturnBestHospitalFromTop5() {
        double lat = 48.85, lon = 2.35;
        int specialityId = 1;

        Hospital h1 = hospital("A", "Alpha", lat + 0.01, lon + 0.01);
        Hospital h2 = hospital("B", "Beta", lat + 0.02, lon + 0.02);
        List<Hospital> hospitals = List.of(h1, h2);

        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 5)).thenReturn(hospitals);
        when(travelTimeService.getTravelTimesBatch(lat, lon, hospitals)).thenReturn(
                Map.of("A", 12, "B", 20)
        );

        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        assertTrue(result.isPresent());
        assertEquals("Alpha", result.get().getName());
    }

    @Test
    void shouldFallbackToTop10WhenTop5Empty() {
        double lat = 48.86, lon = 2.36;
        int specialityId = 2;

        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 5)).thenReturn(List.of());

        Hospital fallback = hospital("F", "Fallback", lat + 0.03, lon + 0.03);
        List<Hospital> fallbackList = List.of(fallback);
        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 10)).thenReturn(fallbackList);
        when(travelTimeService.getTravelTimesBatch(lat, lon, fallbackList)).thenReturn(
                Map.of("F", 15)
        );

        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        assertTrue(result.isPresent());
        assertEquals("Fallback", result.get().getName());
    }

    @Test
    void shouldFallbackToFullScanIfNoneAvailable() {
        double lat = 48.87, lon = 2.37;
        int specialityId = 3;

        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 5)).thenReturn(List.of());
        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 10)).thenReturn(List.of());

        Hospital scan = hospital("Z", "FullScan", lat + 0.05, lon + 0.05);
        when(hospitalRepository.findAll()).thenReturn(List.of(scan));
        when(availabilityService.hasAvailableBedForSpeciality(scan, specialityId)).thenReturn(true);
        when(travelTimeService.getTravelTimesBatch(lat, lon, List.of(scan))).thenReturn(
                Map.of("Z", 9)
        );

        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        assertTrue(result.isPresent());
        assertEquals("FullScan", result.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenNoHospitalsAtAll() {
        double lat = 48.88, lon = 2.38;
        int specialityId = 4;

        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 5)).thenReturn(List.of());
        when(hospitalRepository.findNearestAvailable(lat, lon, specialityId, 10)).thenReturn(List.of());
        when(hospitalRepository.findAll()).thenReturn(List.of());

        Optional<Hospital> result = hospitalLocatorService.findBestHospital(lat, lon, specialityId);

        assertTrue(result.isEmpty());
    }
}
