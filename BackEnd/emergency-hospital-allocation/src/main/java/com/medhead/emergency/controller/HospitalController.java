package com.medhead.emergency.controller;

import com.medhead.emergency.DTO.HospitalDTO;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medhead.emergency.repository.HospitalRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller that exposes endpoints to retrieve hospital data.
 *
 * Uses HospitalDTO to return clean and structured data to clients,
 * hiding internal entity details.
 */
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalRepository hospitalRepository;
    private final ModelMapper modelMapper;

    /**
     * Constructor-based dependency injection for the repository and model mapper.
     *
     * @param hospitalRepository the data source for hospital entities
     * @param modelMapper        maps entities to DTOs
     */
    public HospitalController(HospitalRepository hospitalRepository, ModelMapper modelMapper) {
        this.hospitalRepository = hospitalRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * GET /api/hospitals
     * <p>
     * Retrieves all hospitals from the database and maps them to DTOs.
     *
     * @return a list of HospitalDTO objects
     */
    @GetMapping
    public List<HospitalDTO> getAllHospitals() {
        return hospitalRepository.findAll().stream()
                .map(h -> modelMapper.map(h, HospitalDTO.class)) // Convert each entity to DTO
                .collect(Collectors.toList());
    }
}
