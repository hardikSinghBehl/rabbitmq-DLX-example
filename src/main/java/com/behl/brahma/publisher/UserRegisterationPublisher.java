package com.behl.brahma.publisher;

import java.util.Random;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import com.behl.brahma.configuration.DeclarablesConfiguration;
import com.behl.brahma.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class UserRegisterationPublisher implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final DeclarablesConfiguration declarablesConfiguration;

    @Override
    public void run(String... args) throws JsonProcessingException {
        final var user = UserDto.builder().id(UUID.randomUUID()).age(new Random().nextInt(35))
                .name(Faker.instance().name().fullName()).build();
        final String exchangeName = declarablesConfiguration.userRegisterationExchange().getName();
        final String routingKey = declarablesConfiguration.userRegisterationExchangeAndAdultLogQueueBinding()
                .getRoutingKey();
        rabbitTemplate.convertAndSend(exchangeName, routingKey, objectMapper.writeValueAsString(user));
    }

}
