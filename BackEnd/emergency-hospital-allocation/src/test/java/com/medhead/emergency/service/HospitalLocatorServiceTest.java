package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.entity.Speciality;
import com.medhead.emergency.repository.HospitalRepository;
import com.medhead.emergency.serviceInterface.TravelTimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Field;
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

    @InjectMocks
    private HospitalAvailabilityService availabilityService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        hospitalLocatorService = new HospitalLocatorService();

        // hospitalRepository
        Field repoField = HospitalLocatorService.class.getDeclaredField("hospitalRepository");
        repoField.setAccessible(true);
        repoField.set(hospitalLocatorService, hospitalRepository);

        // travelTimeService
        Field travelField = HospitalLocatorService.class.getDeclaredField("travelTimeService");
        travelField.setAccessible(true);
        travelField.set(hospitalLocatorService, travelTimeService);

        // availabilityService
        Field availField = HospitalLocatorService.class.getDeclaredField("availabilityService");
        availField.setAccessible(true);
        availField.set(hospitalLocatorService, new HospitalAvailabilityService());

        // modelMapper
        Field mapperField = HospitalLocatorService.class.getDeclaredField("modelMapper");
        mapperField.setAccessible(true);
        mapperField.set(hospitalLocatorService, new ModelMapper());
    }

    @Test
    void testFindBestHospital_shouldReturnClosestHospitalWithSpeciality() {
        // Arrange
        double patientLat = 48.8566, patientLon = 2.3522;
        int specialityId = 1;
        Speciality cardio = new Speciality("Cardiology");
        cardio.setId((long) specialityId);

        Hospital h1 = new Hospital();
        h1.setOrgId("HOSP_A");             // ← obligatoire pour le mock findNearest
        h1.setName("Hospital A");
        h1.setLatitude(48.86);
        h1.setLongitude(2.35);
        h1.addSpeciality(cardio, 1);

        Hospital h2 = new Hospital();
        h2.setOrgId("HOSP_B");
        h2.setName("Hospital B");
        h2.setLatitude(48.87);
        h2.setLongitude(2.36);
        h2.addSpeciality(cardio, 2);

        // 1) spatial : on retourne la liste d'IDs
        List<String> nearestIds = List.of("HOSP_A", "HOSP_B");
        when(hospitalRepository.findNearest(patientLat, patientLon, 5))
                .thenReturn(nearestIds);

        // 2) disponibilité après spatial
        when(hospitalRepository.findAvailableByIdsAndSpecialityId(nearestIds, specialityId))
                .thenReturn(List.of(h1, h2));

        // 3) travel times
        when(travelTimeService.getTravelTimeInMinutes(patientLat, patientLon, 48.86, 2.35))
                .thenReturn(10);
        when(travelTimeService.getTravelTimeInMinutes(patientLat, patientLon, 48.87, 2.36))
                .thenReturn(20);

        // Act
        Optional<Hospital> result = hospitalLocatorService
                .findBestHospital(patientLat, patientLon, specialityId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hospital A", result.get().getName());
    }

    @Test
    void testFindBestHospital_shouldReturnEmptyIfNoAvailableBeds() {
        // Arrange
        double patientLat   = 48.85;
        double patientLon   = 2.35;
        int    specialityId = 1;

        Speciality cardio = new Speciality("Cardiology");
        cardio.setId((long) specialityId);

        Hospital h1 = new Hospital();
        h1.setOrgId("HOSP_X");
        h1.setName("NoBeds");
        h1.setLatitude(patientLat);
        h1.setLongitude(patientLon);
        h1.addSpeciality(cardio, 0); // aucun lit dispo

        // primary spatial retourne rien → on passe directement au fallback #2
        when(hospitalRepository.findNearest(patientLat, patientLon, 5))
                .thenReturn(List.of());

        // fallback #1 : pas d'ID non plus
        when(hospitalRepository.findNearest(patientLat, patientLon, 10))
                .thenReturn(List.of());

        // fallback #2 : full scan → retourne h1 (mais 0 lits dispo)
        when(hospitalRepository.findAll())
                .thenReturn(List.of(h1));

        // Act
        Optional<Hospital> result = hospitalLocatorService
                .findBestHospital(patientLat, patientLon, specialityId);

        // Assert
        assertTrue(result.isEmpty(), "On doit obtenir vide si pas de lits dispo");
    }


    @Test
    void testFindBestHospital_shouldReturnHospitalWithShortestTravelTime() {
        // Arrange
        double patientLat = 48.855, patientLon = 2.355;
        int specialityId = 1;
        Speciality cardio = new Speciality("Cardiology");
        cardio.setId((long) specialityId);

        Hospital h1 = new Hospital();
        h1.setOrgId("H1");
        h1.setName("Far but fast");
        h1.setLatitude(48.86);
        h1.setLongitude(2.35);
        h1.addSpeciality(cardio, 1);

        Hospital h2 = new Hospital();
        h2.setOrgId("H2");
        h2.setName("Close but slow");
        h2.setLatitude(48.861);
        h2.setLongitude(2.351);
        h2.addSpeciality(cardio, 1);

        List<String> nearest = List.of("H1", "H2");
        when(hospitalRepository.findNearest(patientLat, patientLon, 5))
                .thenReturn(nearest);
        when(hospitalRepository.findAvailableByIdsAndSpecialityId(nearest, specialityId))
                .thenReturn(List.of(h1, h2));

        when(travelTimeService.getTravelTimeInMinutes(
                anyDouble(), anyDouble(), eq(h1.getLatitude()), eq(h1.getLongitude())))
                .thenReturn(5);
        when(travelTimeService.getTravelTimeInMinutes(
                anyDouble(), anyDouble(), eq(h2.getLatitude()), eq(h2.getLongitude())))
                .thenReturn(15);

        // Act
        Optional<Hospital> result = hospitalLocatorService
                .findBestHospital(patientLat, patientLon, specialityId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Far but fast", result.get().getName());
    }

    @Test
    void testFindBestHospital_withManyHospitals() {
        // Arrange
        double patientLat = 48.85, patientLon = 2.35;
        int specialityId = 1;
        Speciality cardio = new Speciality("Cardiology");
        cardio.setId((long) specialityId);

        List<Hospital> hospitals = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Hospital h = new Hospital();
            String id = "H" + i;
            h.setOrgId(id);
            h.setName("Hospital " + i);
            double lat = 48.85 + (i == 99 ? 0.0001 : 0.01 + i * 0.001);
            double lon = 2.35  + (i == 99 ? 0.0001 : 0.01 + i * 0.001);
            h.setLatitude(lat);
            h.setLongitude(lon);
            h.addSpeciality(cardio, 1);
            hospitals.add(h);

            // travel time stubbing
            when(travelTimeService.getTravelTimeInMinutes(
                    anyDouble(), anyDouble(), eq(lat), eq(lon)))
                    .thenReturn(i == 99 ? 1 : 100 + i);
        }

        // On fait croire que tous sont passés par le tri spatial (même s’il limite à 5 en vrai)
        List<String> nearestIds = hospitals.stream()
                .map(Hospital::getOrgId)
                .toList();
        when(hospitalRepository.findNearest(patientLat, patientLon, 5))
                .thenReturn(nearestIds);
        when(hospitalRepository.findAvailableByIdsAndSpecialityId(nearestIds, specialityId))
                .thenReturn(hospitals);

        // Act
        Optional<Hospital> result = hospitalLocatorService
                .findBestHospital(patientLat, patientLon, specialityId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Hospital 99", result.get().getName());
    }

    @Test
    void allocateBed_shouldCallDecrement_whenBedsAreAvailable() {
        // Arrange
        String hospitalId    = "HOSP-TEST";
        Integer specialityId = 1;

        when(hospitalRepository.decrementAvailableBeds(hospitalId, specialityId)).thenReturn(1);

        // Act & Assert
        assertDoesNotThrow(() -> availabilityService.allocateBed(hospitalId, specialityId));

        verify(hospitalRepository, times(1)).decrementAvailableBeds(hospitalId, specialityId);
    }

    @Test
    void allocateBed_shouldThrowException_whenNoBedsAvailable() {
        // Arrange
        String hospitalId    = "HOSP-TEST";
        Integer specialityId = 1;

        when(hospitalRepository.decrementAvailableBeds(hospitalId, specialityId)).thenReturn(0);

        // Act & Assert
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> availabilityService.allocateBed(hospitalId, specialityId)
        );
        assertEquals("No available beds for speciality id: " + specialityId, ex.getMessage());

        verify(hospitalRepository, times(1)).decrementAvailableBeds(hospitalId, specialityId);
    }
}
