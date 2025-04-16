package com.medhead.emergency.event;

import com.medhead.emergency.entity.Hospital;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Component responsible for publishing bed allocation events using Spring's application event mechanism.
 * This allows the system to notify other components asynchronously when a hospital bed has been allocated.
 */
@Component
public class BedAllocationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Constructs a new BedAllocationEventPublisher with the provided ApplicationEventPublisher.
     *
     * @param applicationEventPublisher the Spring-provided publisher used to broadcast events
     */
    public BedAllocationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Publishes an event indicating that a bed has been allocated at a given hospital for a specific speciality.
     *
     * @param hospital the hospital where the bed was allocated
     * @param speciality the medical speciality associated with the allocated bed
     */
    public void publishBedAllocated(Hospital hospital, String speciality) {
        BedAllocatedEvent event = new BedAllocatedEvent(this, hospital, speciality);
        applicationEventPublisher.publishEvent(event);
    }
}
