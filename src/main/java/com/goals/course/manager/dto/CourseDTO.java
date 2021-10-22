package com.goals.course.manager.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.Set;
import java.util.UUID;

@ToString
@Getter
@Builder
@Jacksonized
public class CourseDTO {
    private final UUID id;
    private final String title;
    private final Set<InstructorDTO> instructors;
    private final Set<LessonDTO> lessons;
    private final Double averageGrade;
    private final Boolean coursePassed;
}
