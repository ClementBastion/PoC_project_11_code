package com.medhead.emergency.DTO;

public class UpdateBedsRequest {
    private String hospitalOrgId;
    private String specialityName;
    private int availableBeds;

    public String getHospitalOrgId() {
        return hospitalOrgId;
    }

    public void setHospitalOrgId(String hospitalOrgId) {
        this.hospitalOrgId = hospitalOrgId;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String specialityName) {
        this.specialityName = specialityName;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }
}
