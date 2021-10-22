package com.goals.course.manager.service.validation.implementation;

import com.goals.course.manager.dao.entity.CourseStudent;
import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.service.validation.CourseAssignmentValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseAssignmentValidationImpl implements CourseAssignmentValidation {
    @Value("${app.student.maxNumberOfCourses}")
    private Integer maxNumberOfCoursesStudentCanTake;

    @Override
    public ValidationResult canStudentTakeACourse(final Student student) {
        final var notCompletedCourses = student.getCourses()
                .stream()
                .filter(CourseStudent::courseIsNotCompleted)
                .toList();

        if (notCompletedCourses.size() >= maxNumberOfCoursesStudentCanTake) {
            final var message = "Student cannot take more courses. Maximum is %d".formatted(maxNumberOfCoursesStudentCanTake);
            return ValidationResult.invalid(message);
        }

        return ValidationResult.valid();
    }
}
