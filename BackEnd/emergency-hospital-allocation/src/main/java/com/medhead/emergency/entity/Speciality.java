package com.medhead.emergency.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name; // e.g., "Cardiology", "Oncology", etc.

    @OneToMany(mappedBy = "speciality", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HospitalSpeciality> hospitalSpecialities = new ArrayList<>();


    public Speciality() {}

    public Speciality(String name) {
        this.name = name;
    }

    // Getters and Setters
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

}
