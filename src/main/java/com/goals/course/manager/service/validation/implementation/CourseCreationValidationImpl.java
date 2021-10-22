package com.goals.course.manager.service.validation.implementation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.CourseCreationValidation;
import com.goals.course.manager.service.validation.course.CourseValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseCreationValidationImpl implements CourseCreationValidation {
    private final List<CourseValidation> validations;

    @Override
    public ValidationResult validateCreation(final CourseDTO course) {
        log.info("Validate course before creation");
        return validations
                .stream()
                .map(validator -> validator.validate(course))
                .filter(ValidationResult::isNotValid)
                .findFirst()
                .orElseGet(ValidationResult::valid);
    }
}
