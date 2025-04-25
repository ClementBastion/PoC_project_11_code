package com.medhead.emergency.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that listens for hospital bed allocation events and processes them.
 * This component is responsible for handling messages received from the Kafka topic
 * "hospital.bed.allocated" and logging their content for further processing.
 */
@Component
public class BedAllocationKafkaConsumer {

    /**
     * Kafka listener method for the topic "hospital.bed.allocated".
     * It receives messages serialized as BedAllocatedMessageDTO and logs the allocation details.
     *
     * @param record the complete Kafka ConsumerRecord containing the message key, value, and metadata
     */
    @KafkaListener(
            topics = "hospital.bed.allocated",
            groupId = "emergency-service-group",
            containerFactory = "bedAllocatedMessageListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, BedAllocatedMessageDTO> record) {
        BedAllocatedMessageDTO message = record.value();

        // Log the content of the received Kafka message
        System.out.printf(
                "🔔 [Kafka] Bed allocation received: hospitalId=%s, name=%s, speciality=%s%n",
                message.getHospitalId(),
                message.getHospitalName(),
                message.getSpeciality()
        );
    }
}
