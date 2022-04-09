package com.goals.course.manager.service;

import java.util.UUID;

public interface CourseAssigmentService {
    void assignStudentToCourse(final UUID courseId, final UUID userId);

    void assignInstructorToCourse(final UUID courseId, final UUID userId);
}
