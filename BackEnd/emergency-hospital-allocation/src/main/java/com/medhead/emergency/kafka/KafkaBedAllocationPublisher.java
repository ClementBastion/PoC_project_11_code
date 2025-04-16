package com.medhead.emergency.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer service that publishes hospital bed allocation messages
 * to the "hospital.bed.allocated" topic.
 */
@Service
public class KafkaBedAllocationPublisher {

    private static final String TOPIC = "hospital.bed.allocated";

    private final KafkaTemplate<String, BedAllocatedMessageDTO> kafkaTemplate;

    /**
     * Constructor for dependency injection of KafkaTemplate.
     *
     * @param kafkaTemplate the Kafka template used to send messages
     */
    public KafkaBedAllocationPublisher(KafkaTemplate<String, BedAllocatedMessageDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a hospital bed allocation message to the Kafka topic.
     *
     * @param message the message payload containing bed allocation details
     */
    public void publish(BedAllocatedMessageDTO message) {
        // Log the message being sent (can be replaced by a proper logger)
        System.out.println("Kafka PUBLISH: " + message);

        // Send the message to the Kafka topic with hospitalId as the key
        kafkaTemplate.send(TOPIC, message.getHospitalId(), message);
    }
}
