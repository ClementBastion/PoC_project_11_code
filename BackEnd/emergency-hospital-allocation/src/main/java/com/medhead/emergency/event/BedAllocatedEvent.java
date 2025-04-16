package com.medhead.emergency.event;

import com.medhead.emergency.entity.Hospital;
import org.springframework.context.ApplicationEvent;

/**
 * Event representing the allocation of a hospital bed for a specific medical speciality.
 * This event can be published to notify other components of the system about the allocation.
 */
public class BedAllocatedEvent extends ApplicationEvent {

    private final Hospital hospital;
    private final String speciality;

    /**
     * Creates a new BedAllocatedEvent.
     *
     * @param source the component that published the event (typically the service performing the allocation)
     * @param hospital the hospital where the bed was allocated
     * @param speciality the medical speciality for which the bed was allocated
     */
    public BedAllocatedEvent(Object source, Hospital hospital, String speciality) {
        super(source);
        this.hospital = hospital;
        this.speciality = speciality;
    }

    /**
     * Returns the hospital where the bed was allocated.
     *
     * @return the Hospital entity
     */
    public Hospital getHospital() {
        return hospital;
    }

    /**
     * Returns the medical speciality for which the bed was allocated.
     *
     * @return the name of the speciality
     */
    public String getSpeciality() {
        return speciality;
    }
}
