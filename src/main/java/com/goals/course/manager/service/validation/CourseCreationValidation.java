package com.goals.course.manager.service.validation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.implementation.ValidationResult;

public interface CourseCreationValidation {
    ValidationResult validateCreation(final CourseDTO course);
}
