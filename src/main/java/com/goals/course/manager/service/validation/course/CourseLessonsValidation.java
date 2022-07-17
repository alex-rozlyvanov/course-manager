package com.goals.course.manager.service.validation.course;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.ValidationResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class CourseLessonsValidation implements CourseValidation {
    @Value("${app.course.lessons.numberOfRequiredLessons}")
    private Integer numberOfRequiredLessons;

    public ValidationResult validate(final CourseDTO courseDTO) {
        final var lessons = courseDTO.getLessons();

        if (isNull(lessons) || lessons.isEmpty() || lessons.size() < numberOfRequiredLessons) {
            return ValidationResult.invalid("Course should have at least 5 lessons");
        }
        return ValidationResult.valid();
    }
}
