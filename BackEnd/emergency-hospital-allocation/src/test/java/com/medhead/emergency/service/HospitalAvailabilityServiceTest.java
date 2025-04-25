package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.entity.HospitalSpeciality;
import com.medhead.emergency.entity.Speciality;
import com.medhead.emergency.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HospitalAvailabilityServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalAvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void hasAvailableBedForSpeciality_shouldReturnTrue_whenBedsAvailable() {
        // Arrange
        Hospital hospital = new Hospital();
        Speciality speciality = new Speciality("Cardiology");
        speciality.setId(1);

        HospitalSpeciality hs = new HospitalSpeciality();
        hs.setHospital(hospital);
        hs.setSpeciality(speciality);
        hs.setAvailableBeds(3);

        hospital.setHospitalSpecialities(List.of(hs));

        // Act
        boolean result = availabilityService.hasAvailableBedForSpeciality(hospital, 1);

        // Assert
        assertTrue(result);
    }

    @Test
    void hasAvailableBedForSpeciality_shouldReturnFalse_whenNoBedsAvailable() {
        Hospital hospital = new Hospital();
        Speciality speciality = new Speciality("Cardiology");
        speciality.setId(1);

        HospitalSpeciality hs = new HospitalSpeciality();
        hs.setHospital(hospital);
        hs.setSpeciality(speciality);
        hs.setAvailableBeds(0);

        hospital.setHospitalSpecialities(List.of(hs));

        boolean result = availabilityService.hasAvailableBedForSpeciality(hospital, 1);

        assertFalse(result);
    }

    @Test
    void hasAvailableBedForSpeciality_shouldReturnFalse_whenSpecialityNotFound() {
        Hospital hospital = new Hospital();
        Speciality speciality = new Speciality("Neurology");
        speciality.setId(2);

        HospitalSpeciality hs = new HospitalSpeciality();
        hs.setHospital(hospital);
        hs.setSpeciality(speciality);
        hs.setAvailableBeds(4);

        hospital.setHospitalSpecialities(List.of(hs));

        boolean result = availabilityService.hasAvailableBedForSpeciality(hospital, 1);

        assertFalse(result);
    }

    @Test
    void allocateBed_shouldDecrement_whenBedsAvailable() {
        when(hospitalRepository.decrementAvailableBeds("HOSP_X", 1)).thenReturn(1);

        assertDoesNotThrow(() -> availabilityService.allocateBed("HOSP_X", 1));

        verify(hospitalRepository, times(1)).decrementAvailableBeds("HOSP_X", 1);
    }

    @Test
    void allocateBed_shouldThrow_whenNoBedsAvailable() {
        when(hospitalRepository.decrementAvailableBeds("HOSP_Y", 2)).thenReturn(0);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> availabilityService.allocateBed("HOSP_Y", 2)
        );

        assertEquals("No available beds for speciality id: 2", ex.getMessage());
        verify(hospitalRepository).decrementAvailableBeds("HOSP_Y", 2);
    }
}
