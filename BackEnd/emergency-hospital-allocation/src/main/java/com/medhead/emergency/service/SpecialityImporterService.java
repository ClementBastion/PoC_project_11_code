package com.medhead.emergency.service;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.entity.Speciality;
import com.medhead.emergency.entity.HospitalSpeciality;
import com.medhead.emergency.repository.HospitalRepository;
import com.medhead.emergency.repository.HospitalSpecialityRepository;
import com.medhead.emergency.repository.SpecialityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SpecialityImporterService {

    private static final Logger logger = LoggerFactory.getLogger(SpecialityImporterService.class);

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    @Autowired
    private HospitalSpecialityRepository hospitalSpecialityRepository;

    /**
     * Reads specialities from a CSV and assigns each randomly to hospitals with random available beds.
     */
    public void importAndAssignRandomlyFromCsv() {
        logger.info("📥 Starting speciality import and random hospital assignment...");

        try {
            InputStream csvInputStream = new ClassPathResource("static/nhs_specialities.csv").getInputStream();
            List<Hospital> hospitals = hospitalRepository.findAll();
            if (hospitals.isEmpty()) throw new IllegalStateException("❌ No hospitals found in database");

            logger.info("🏥 Found {} hospitals in database", hospitals.size());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvInputStream))) {
                reader.lines().skip(1).forEach(line -> {
                    String specialityName = line.trim();
                    if (specialityName.isBlank()) return;

                    logger.info("➡️ Processing speciality: {}", specialityName);

                    // Create or reuse speciality
                    Speciality speciality = specialityRepository.findByNameIgnoreCase(specialityName)
                            .orElseGet(() -> {
                                logger.info("🆕 Creating new speciality: {}", specialityName);
                                return specialityRepository.save(new Speciality(specialityName));
                            });

                    // Randomly assign this speciality to 1 to N hospitals
                    Collections.shuffle(hospitals);
                    int numAssignments = ThreadLocalRandom.current().nextInt(1, Math.min(5, hospitals.size()) + 1);

                    logger.info("🔗 Assigning speciality '{}' to {} hospital(s)", specialityName, numAssignments);

                    for (int i = 0; i < numAssignments; i++) {
                        Hospital hospital = hospitals.get(i);
                        int availableBeds = ThreadLocalRandom.current().nextInt(0, 20); // e.g. 0 to 19

                        HospitalSpeciality hs = new HospitalSpeciality();
                        hs.setHospital(hospital);
                        hs.setSpeciality(speciality);
                        hs.setAvailableBeds(availableBeds);

                        hospitalSpecialityRepository.save(hs);

                        logger.debug("🏥 Assigned to hospital '{}' [{}] with {} bed(s)", hospital.getName(), hospital.getOrgId(), availableBeds);
                    }
                });

                logger.info("✅ Speciality import and assignment completed.");
            }

        } catch (IOException e) {
            logger.error("❌ Failed to load or parse the CSV file", e);
            throw new RuntimeException("Could not load specialities CSV", e);
        }
    }
}
