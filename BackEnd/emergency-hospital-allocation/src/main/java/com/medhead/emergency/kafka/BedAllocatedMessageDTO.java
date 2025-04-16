package com.medhead.emergency.kafka;

public class BedAllocatedMessageDTO {

    private String hospitalId;
    private String hospitalName;
    private String speciality;

    public BedAllocatedMessageDTO() {}

    public BedAllocatedMessageDTO(String hospitalId, String hospitalName, String speciality) {
        this.hospitalId = hospitalId;
        this.hospitalName = hospitalName;
        this.speciality = speciality;
    }

    // Getters & setters
    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
}
