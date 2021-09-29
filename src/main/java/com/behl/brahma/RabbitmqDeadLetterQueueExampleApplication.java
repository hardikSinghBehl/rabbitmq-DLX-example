package com.behl.brahma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class RabbitmqDeadLetterQueueExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmqDeadLetterQueueExampleApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
