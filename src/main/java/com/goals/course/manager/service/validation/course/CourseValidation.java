package com.goals.course.manager.service.validation.course;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.implementation.ValidationResult;

public interface CourseValidation {
    ValidationResult validate(final CourseDTO courseDTO);
}
