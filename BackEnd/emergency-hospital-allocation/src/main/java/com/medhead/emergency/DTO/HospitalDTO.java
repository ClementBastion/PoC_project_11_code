package com.medhead.emergency.DTO;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Data Transfer Object (DTO) for exposing hospital information
 * to external clients (e.g. frontend or REST API consumers).
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

    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getAddressLine3() { return addressLine3; }
    public void setAddressLine3(String addressLine3) { this.addressLine3 = addressLine3; }

    public String getTown() { return town; }
    public void setTown(String town) { this.town = town; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getUprn() { return uprn; }
    public void setUprn(String uprn) { this.uprn = uprn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(Date lastChangeDate) { this.lastChangeDate = lastChangeDate; }

    public String getPrimaryRoleId() { return primaryRoleId; }
    public void setPrimaryRoleId(String primaryRoleId) { this.primaryRoleId = primaryRoleId; }

    public String getOrgLink() { return orgLink; }
    public void setOrgLink(String orgLink) { this.orgLink = orgLink; }

    /**
     * Utility method that combines address fields into a readable full address.
     * Skips empty/null fields.
     *
     * @return a comma-separated full address string
     */
    public String getFullAddress() {
        return Stream.of(addressLine1, addressLine2, addressLine3, town, postcode)
                .filter(part -> part != null && !part.isBlank())
                .collect(Collectors.joining(", "));
    }
}
