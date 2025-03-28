package com.medhead.emergency.controller;

import com.medhead.emergency.DTO.UpdateBedsRequest;
import com.medhead.emergency.entity.HospitalSpeciality;
import com.medhead.emergency.entity.Speciality;
import com.medhead.emergency.repository.HospitalSpecialityRepository;
import com.medhead.emergency.repository.SpecialityRepository;
import com.medhead.emergency.service.SpecialityImporterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/specialities")
public class SpecialityController {

    private final SpecialityRepository specialityRepository;
    private final HospitalSpecialityRepository hospitalSpecialityRepository;
    private final SpecialityImporterService specialityImporterService;

    public SpecialityController(SpecialityRepository specialityRepository,
                                HospitalSpecialityRepository hospitalSpecialityRepository,
                                SpecialityImporterService specialityImporterService) {
        this.specialityRepository = specialityRepository;
        this.hospitalSpecialityRepository = hospitalSpecialityRepository;
        this.specialityImporterService = specialityImporterService;
    }

    /**
     * Trigger the CSV import and random assignment of specialities to hospitals.
     */
    @PostMapping("/import")
    public String importSpecialitiesAndAssignToHospitals() {
        specialityImporterService.importAndAssignRandomlyFromCsv();
        return "✅ Specialities imported and assigned successfully.";
    }

    /**
     * Get all specialities.
     */
    @GetMapping
    public List<Speciality> getAllSpecialities() {
        return specialityRepository.findAll();
    }

    /**
     * Get all hospital-speciality links (with available beds).
     */
    @GetMapping("/assignments")
    public List<HospitalSpeciality> getAllAssignments() {
        return hospitalSpecialityRepository.findAll();
    }

    /**
     * Get all specialities for a given hospital.
     */
    @GetMapping("/hospital/{orgId}")
    public List<HospitalSpeciality> getSpecialitiesByHospital(@PathVariable String orgId) {
        return hospitalSpecialityRepository.findAll()
                .stream()
                .filter(hs -> hs.getHospital().getOrgId().equalsIgnoreCase(orgId))
                .toList();
    }
    /**
     * Update the number of available beds for a given hospital-speciality.
     * Only accessible to users with roles: ADMIN or HOSPITAL_MANAGER.
     */
    @PutMapping("/assignments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOSPITAL_MANAGER')")
    public ResponseEntity<?> updateAvailableBeds(@RequestBody UpdateBedsRequest request) {
        if (request.getAvailableBeds() < 0) {
            return ResponseEntity.badRequest().body("❌ Available beds cannot be negative.");
        }

        Optional<HospitalSpeciality> optional = hospitalSpecialityRepository.findAll().stream()
                .filter(hs ->
                        hs.getHospital().getOrgId().equalsIgnoreCase(request.getHospitalOrgId()) &&
                                hs.getSpeciality().getName().equalsIgnoreCase(request.getSpecialityName()))
                .findFirst();

        if (optional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("❌ No matching hospital-speciality assignment found.");
        }

        HospitalSpeciality hs = optional.get();
        hs.setAvailableBeds(request.getAvailableBeds());
        hospitalSpecialityRepository.save(hs);

        return ResponseEntity.ok(hs);
    }
}
