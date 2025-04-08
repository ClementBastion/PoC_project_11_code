package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.entity.Speciality;
import com.medhead.emergency.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalLocatorServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @Mock
    private TravelTimeService travelTimeService;

    @InjectMocks
    private HospitalLocatorService hospitalLocatorService;

    @BeforeEach
    void setup() {
        // Initialize Mockito mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindBestHospital_shouldReturnClosestHospitalWithSpeciality() {
        // Arrange
        double patientLat = 48.8566;
        double patientLon = 2.3522;
        String specialityName = "Cardiology";

        // Create a speciality
        Speciality cardio = new Speciality(specialityName);

        // Create two hospitals with available beds in the given speciality
        Hospital h1 = new Hospital();
        h1.setName("Hospital A");
        h1.setLatitude(48.86);
        h1.setLongitude(2.35);
        h1.addSpeciality(cardio, 1);

        Hospital h2 = new Hospital();
        h2.setName("Hospital B");
        h2.setLatitude(48.87);
        h2.setLongitude(2.36);
        h2.addSpeciality(cardio, 2);

        List<Hospital> allHospitals = List.of(h1, h2);

        // Mock repository and travel time service
        when(hospitalRepository.findAll()).thenReturn(allHospitals);
        when(travelTimeService.getTravelTimeInMinutes(patientLat, patientLon, 48.86, 2.35)).thenReturn(10);
        when(travelTimeService.getTravelTimeInMinutes(patientLat, patientLon, 48.87, 2.36)).thenReturn(20);

        // Act
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(patientLat, patientLon, specialityName);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hospital A", result.get().getName());
    }

    @Test
    void testFindBestHospital_shouldReturnEmptyIfNoAvailableBeds() {
        // Arrange
        String speciality = "Cardiology";
        Speciality cardio = new Speciality(speciality);

        Hospital h1 = new Hospital();
        h1.setName("Hospital Without Beds");
        h1.setLatitude(48.85);
        h1.setLongitude(2.35);
        h1.addSpeciality(cardio, 0); // No available beds

        when(hospitalRepository.findAll()).thenReturn(List.of(h1));

        // Act
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(48.85, 2.35, speciality);

        // Assert
        assertTrue(result.isEmpty(), "No hospital should be returned when none has available beds");
    }

    @Test
    void testFindBestHospital_shouldReturnHospitalWithShortestTravelTime() {
        // Arrange
        String speciality = "Cardiology";
        Speciality cardio = new Speciality(speciality);

        // Hospital 1 is further but faster
        Hospital h1 = new Hospital();
        h1.setName("Far but fast");
        h1.setLatitude(48.86);
        h1.setLongitude(2.35);
        h1.addSpeciality(cardio, 1);

        // Hospital 2 is closer but slower
        Hospital h2 = new Hospital();
        h2.setName("Close but slow");
        h2.setLatitude(48.861);
        h2.setLongitude(2.351);
        h2.addSpeciality(cardio, 1);

        List<Hospital> allHospitals = List.of(h1, h2);

        when(hospitalRepository.findAll()).thenReturn(allHospitals);
        when(travelTimeService.getTravelTimeInMinutes(anyDouble(), anyDouble(), eq(h1.getLatitude()), eq(h1.getLongitude())))
                .thenReturn(5);
        when(travelTimeService.getTravelTimeInMinutes(anyDouble(), anyDouble(), eq(h2.getLatitude()), eq(h2.getLongitude())))
                .thenReturn(15);

        // Act
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(48.855, 2.355, speciality);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Far but fast", result.get().getName());
    }

    @Test
    void testFindBestHospital_withManyHospitals() {
        // Arrange
        String speciality = "Cardiology";
        Speciality cardio = new Speciality(speciality);

        List<Hospital> hospitals = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Hospital h = new Hospital();
            h.setName("Hospital " + i);

            // Ensure hospital 99 is one of the closest (haversine) to be included in the final comparison
            double lat = 48.85 + (i == 99 ? 0.0001 : 0.01 + i * 0.001);
            double lon = 2.35 + (i == 99 ? 0.0001 : 0.01 + i * 0.001);

            h.setLatitude(lat);
            h.setLongitude(lon);
            h.addSpeciality(cardio, 1);
            hospitals.add(h);

            // Assign a better travel time to hospital 99
            when(travelTimeService.getTravelTimeInMinutes(anyDouble(), anyDouble(), eq(lat), eq(lon)))
                    .thenReturn(i == 99 ? 1 : 100 + i);
        }

        when(hospitalRepository.findAll()).thenReturn(hospitals);

        // Act
        Optional<Hospital> result = hospitalLocatorService.findBestHospital(48.85, 2.35, speciality);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hospital 99", result.get().getName());
    }
}
