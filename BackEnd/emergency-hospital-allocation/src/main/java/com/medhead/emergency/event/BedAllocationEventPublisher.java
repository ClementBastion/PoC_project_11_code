package com.medhead.emergency.event;

import com.medhead.emergency.entity.Hospital;
import com.medhead.emergency.kafka.BedAllocatedMessageDTO;
import com.medhead.emergency.kafka.KafkaBedAllocationPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for publishing hospital bed allocation events.
 * It broadcasts the event internally using Spring's event system and externally through Kafka.
 */
@Component
public class BedAllocationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final KafkaBedAllocationPublisher kafkaPublisher;

    /**
     * Constructs a BedAllocationEventPublisher with the required publishers.
     *
     * @param applicationEventPublisher the internal Spring event publisher
     * @param kafkaPublisher the Kafka event publisher for external messaging
     */
    public BedAllocationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher,
            KafkaBedAllocationPublisher kafkaPublisher
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.kafkaPublisher = kafkaPublisher;
    }

    /**
     * Publishes a bed allocation event both internally (Spring) and externally (Kafka).
     *
     * @param hospital the hospital where the bed has been allocated
     * @param specialityId the medical speciality for which the bed has been allocated
     */
    public void publishBedAllocated(Hospital hospital, Integer specialityId) {
        // Publish internal Spring event for local listeners
        BedAllocatedEvent event = new BedAllocatedEvent(this, hospital, specialityId);
        applicationEventPublisher.publishEvent(event);

        // Publish an external Kafka message for distributed systems or integration
        BedAllocatedMessageDTO message = new BedAllocatedMessageDTO(
                hospital.getOrgId(),
                hospital.getName(),
                specialityId
        );
        kafkaPublisher.publish(message);
    }
}
