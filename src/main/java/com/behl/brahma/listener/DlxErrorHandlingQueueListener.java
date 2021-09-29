package com.behl.brahma.listener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;

import com.behl.brahma.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class DlxErrorHandlingQueueListener {

    @RabbitListener(queues = "dlx-non-adult-handing-queue")
    public void listenToDlxErrorHandlingQueue(String message) throws IOException {
        final var user = new ObjectMapper().readValue(message, UserDto.class);
        log.warn(
                "User with id {} and age {}, was pushed to adult-user-log-queue at {}, Shouldn't be pushed because the user is a minor, Freeze the users account until further inspection",
                user.getId(), user.getAge(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd hh:ss:mm").format(LocalDateTime.now(ZoneId.of("+00:00"))));
    }

}
