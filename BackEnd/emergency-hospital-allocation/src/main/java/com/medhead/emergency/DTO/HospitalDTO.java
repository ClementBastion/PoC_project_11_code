package com.medhead.emergency.DTO;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for exposing hospital information
 * to external clients (e.g., frontend or REST API consumers).
 * <p>
 * This class mirrors the Hospital entity structure but is intended
 * for safe and structured data exposure, without persistence logic.
 */
public class HospitalDTO {

    // Unique identifier of the hospital (usually from NHS)
    private String orgId;

    // Name of the hospital
    private String name;

    // Detailed address fields
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String town;
    private String postcode;
    private String country;
    private String uprn; // Unique Property Reference Number

    // Metadata fields
    private String status;
    private Date lastChangeDate;
    private String primaryRoleId;
    private String orgLink;

    // Default constructor (required for serialization/deserialization)
    public HospitalDTO() {}

    // 🔹 Getters and Setters

}
