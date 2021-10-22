package com.goals.course.manager.service.validation;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.service.validation.implementation.ValidationResult;

public interface CourseAssignmentValidation {
    ValidationResult canStudentTakeACourse(final Student student);
}
