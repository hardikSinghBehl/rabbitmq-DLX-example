package com.behl.brahma.listener;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.behl.brahma.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdultUserLogQueueListener {

    @RabbitListener(queues = "adult-user-logging-queue", concurrency = "3-8")
    public void listenToUserRegisteration(String message) throws JsonMappingException, JsonProcessingException {
        UserDto user = new ObjectMapper().readValue(message, UserDto.class);

        if (user.getAge() < 18) {
            log.error("User receieved is not an adult: {}", user.getId());
            throw new AmqpRejectAndDontRequeueException("User's a minor");
        }

        log.info("User is an adult, will continue processing");
    }

}
