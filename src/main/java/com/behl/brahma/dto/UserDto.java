package com.behl.brahma.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UserDto {

    private final UUID id;
    private final String name;
    private final Integer age;

}
