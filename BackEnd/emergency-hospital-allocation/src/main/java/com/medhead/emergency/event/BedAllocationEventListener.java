package com.medhead.emergency.event;

import com.medhead.emergency.service.HospitalAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * This class listens for BedAllocatedEvent events and handles them accordingly.
 * It serves as an event subscriber in the Spring event-driven architecture.
 */
@Component
public class BedAllocationEventListener {

    @Autowired
    private HospitalAvailabilityService availabilityService;

    /**
     * Handles BedAllocatedEvent when a bed is successfully allocated to a hospital.
     * Logs a confirmation message with hospital name and speciality.
     *
     * @param event the event containing the hospital and the speciality for which the bed was allocated
     */
    @EventListener
    public void handleBedAllocatedEvent(BedAllocatedEvent event) {

        availabilityService.allocateBed(event.getHospital().getOrgId(), event.getSpecialityId());

        System.out.printf("📢 Bed successfully allocated at hospital: %s (speciality: %s)%n",
                event.getHospital().getName(), event.getSpecialityId());
    }
}
