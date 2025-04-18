package com.medhead.emergency.event;

import com.medhead.emergency.entity.Hospital;
import org.springframework.context.ApplicationEvent;

/**
 * Event representing the allocation of a hospital bed for a specific medical speciality.
 * This event can be published to notify other components of the system about the allocation.
 */
public class BedAllocatedEvent extends ApplicationEvent {

    private final Hospital hospital;
    private final Integer specialityId;

    /**
     * Creates a new BedAllocatedEvent.
     *
     * @param source the component that published the event (typically the service performing the allocation)
     * @param hospital the hospital where the bed was allocated
     * @param specialityId the medical speciality for which the bed was allocated
     */
    public BedAllocatedEvent(Object source, Hospital hospital, Integer specialityId) {
        super(source);
        this.hospital = hospital;
        this.specialityId = specialityId;
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
    public Integer getSpecialityId() {
        return specialityId;
    }
}
