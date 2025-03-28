package com.medhead.emergency.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity  // Marks this class as a JPA entity mapped to a database table
public class Hospital {

    @Id  // Primary key for the entity
    private String orgId;

    // Basic hospital information
    private String name;
    private String status;
    private Date lastChangeDate;
    private String primaryRoleId;
    private String orgLink;

    // Address fields
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String town;
    private String postcode;
    private String country;
    private String uprn;  // Unique Property Reference Number

    // Default constructor required by JPA
    public Hospital() {}

    /**
     * All-args constructor to create a fully-initialized Hospital object.
     */
    public Hospital(String orgId, String name, String addressLine1, String addressLine2, String addressLine3,
                    String town, String postcode, String country, String uprn,
                    String status, Date lastChangeDate, String primaryRoleId, String orgLink) {
        this.orgId = orgId;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.addressLine3 = addressLine3;
        this.town = town;
        this.postcode = postcode;
        this.country = country;
        this.uprn = uprn;
        this.status = status;
        this.lastChangeDate = lastChangeDate;
        this.primaryRoleId = primaryRoleId;
        this.orgLink = orgLink;
    }

    // Getters and setters for all fields
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
     * Utility method that returns a concatenated full address
     * Skips any blank or null components
     */
    public String getFullAddress() {
        return Stream.of(addressLine1, addressLine2, addressLine3, town, postcode)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(", "));
    }
}
