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
public class LessonDTO {
    private final UUID id;
    private final String title;
    private final GradeDTO gradeDTO;
}
