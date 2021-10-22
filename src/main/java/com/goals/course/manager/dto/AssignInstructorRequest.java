package com.goals.course.manager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class AssignInstructorRequest {
    @NotNull
    private final UUID instructorId;
}
