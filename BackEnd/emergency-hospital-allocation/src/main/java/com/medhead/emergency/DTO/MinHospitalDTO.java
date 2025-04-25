package com.medhead.emergency.DTO;

import com.medhead.emergency.entity.Hospital;

public record MinHospitalDTO(String name, Double latitude, Double longitude) {
    public static MinHospitalDTO fromEntity(Hospital h) {
        return new MinHospitalDTO(h.getName(), h.getLatitude(), h.getLongitude());
    }
}