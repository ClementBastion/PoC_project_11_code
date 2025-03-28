package com.medhead.emergency.entity;

import jakarta.persistence.*;

@Entity
public class HospitalSpeciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Hospital hospital;

    @ManyToOne
    private Speciality speciality;

    private int availableBeds;

    public HospitalSpeciality() {
    }
    public HospitalSpeciality(Long id, Hospital hospital, Speciality speciality, int availableBeds) {
        this.id = id;
        this.hospital = hospital;
        this.speciality = speciality;
        this.availableBeds = availableBeds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public Speciality getSpeciality() {
        return speciality;
    }

    public void setSpeciality(Speciality speciality) {
        this.speciality = speciality;
    }

    public int getAvailableBeds() {
        return availableBeds;
    }

    public void setAvailableBeds(int availableBeds) {
        this.availableBeds = availableBeds;
    }

}
