package com.medhead.emergency.config;

import com.medhead.emergency.kafka.BedAllocatedMessageDTO;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.Map;

@Configuration
public class KafkaConfig {

    /**
     * Creates a listener container factory for Kafka consumers handling BedAllocatedMessageDTO.
     * This is used by @KafkaListener to consume and deserialize messages from Kafka.
     *
     * @param consumerFactory the configured consumer factory for BedAllocatedMessageDTO
     * @return the listener container factory
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BedAllocatedMessageDTO> bedAllocatedMessageListenerContainerFactory(
            ConsumerFactory<String, BedAllocatedMessageDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, BedAllocatedMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    /**
     * Creates a Kafka ConsumerFactory configured to deserialize keys as strings
     * and values as BedAllocatedMessageDTO using JSON deserialization.
     *
     * @return the configured Kafka ConsumerFactory
     */
    @Bean
    public ConsumerFactory<String, BedAllocatedMessageDTO> bedAllocatedMessageConsumerFactory() {
        JsonDeserializer<BedAllocatedMessageDTO> jsonDeserializer = new JsonDeserializer<>(BedAllocatedMessageDTO.class);
        jsonDeserializer.addTrustedPackages("*"); // Trust all packages (adjust as needed for security)

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
                        ConsumerConfig.GROUP_ID_CONFIG, "emergency-service-group",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                jsonDeserializer
        );
    }
}
