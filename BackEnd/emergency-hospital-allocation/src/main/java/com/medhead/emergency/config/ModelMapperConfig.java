package com.medhead.emergency.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the ModelMapper bean.
 * <p>
 * ModelMapper is used to map between entity objects and DTOs.
 * This bean will be available throughout the Spring context.
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Declares a ModelMapper bean to be used for object mapping.
     *
     * @return a new instance of ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
