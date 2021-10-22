package com.goals.course.manager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class RoleDTO {
    private final UUID id;
    private final String title;
}
