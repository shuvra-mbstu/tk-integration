package com.tk.integration.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }
}