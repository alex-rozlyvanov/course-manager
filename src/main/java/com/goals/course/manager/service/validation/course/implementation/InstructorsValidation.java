package com.goals.course.manager.service.validation.course.implementation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.course.CourseValidation;
import com.goals.course.manager.service.validation.implementation.ValidationResult;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class InstructorsValidation implements CourseValidation {
    @Override
    public ValidationResult validate(final CourseDTO courseDTO) {
        final var instructors = courseDTO.getInstructors();

        if (isNull(instructors) || instructors.isEmpty()) {
            return ValidationResult.invalid("Course should have at least one instructor");
        }

        return ValidationResult.valid();
    }
}
