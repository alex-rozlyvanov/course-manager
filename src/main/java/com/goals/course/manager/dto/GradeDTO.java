package com.goals.course.manager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@ToString
@Getter
@Builder
@Jacksonized
public class GradeDTO {
    private final UUID id;
    private final UUID lessonId;
    private final UUID studentId;
    private final Integer grade;
}
