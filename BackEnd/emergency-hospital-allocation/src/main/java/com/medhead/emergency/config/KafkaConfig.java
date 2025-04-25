package com.medhead.emergency.config;

import com.medhead.emergency.kafka.BedAllocatedMessageDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BedAllocatedMessageDTO> bedAllocatedMessageListenerContainerFactory(
            ConsumerFactory<String, BedAllocatedMessageDTO> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, BedAllocatedMessageDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BedAllocatedMessageDTO> bedAllocatedMessageConsumerFactory() {
        JsonDeserializer<BedAllocatedMessageDTO> jsonDeserializer = new JsonDeserializer<>(BedAllocatedMessageDTO.class);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                Map.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers, // ✅ ici maintenant dynamique
                        ConsumerConfig.GROUP_ID_CONFIG, "emergency-service-group",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                jsonDeserializer
        );
    }
}
