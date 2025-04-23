package com.medhead.emergency.kafka;

public class BedAllocatedMessageDTO {

    private String hospitalId;
    private String hospitalName;
    private Integer specialityId;

    public BedAllocatedMessageDTO() {}

    public BedAllocatedMessageDTO(String hospitalId, String hospitalName, Integer specialityId) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.specialityId = specialityId;
    }

    // Getters & setters
    public String getHospitalId() {
        return hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public Integer getSpeciality() {
        return specialityId;
    }

}
