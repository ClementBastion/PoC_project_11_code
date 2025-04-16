package com.medhead.emergency.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class listens for BedAllocatedEvent events and handles them accordingly.
 * It serves as an event subscriber in the Spring event-driven architecture.
 */
@Component
public class BedAllocationEventListener {

    /**
     * Handles BedAllocatedEvent when a bed is successfully allocated to a hospital.
     * Logs a confirmation message with hospital name and speciality.
     *
     * @param event the event containing the hospital and the speciality for which the bed was allocated
     */
    @EventListener
    public void handleBedAllocatedEvent(BedAllocatedEvent event) {
        System.out.printf("📢 Bed successfully allocated at hospital: %s (speciality: %s)%n",
                event.getHospital().getName(), event.getSpeciality());
    }
}
