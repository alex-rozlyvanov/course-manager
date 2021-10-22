package com.goals.course.manager.controller;

import com.goals.course.manager.dto.AssignStudentToCourseRequest;

import java.util.UUID;

public interface CourseAssigmentController {
    void assignUserToCourse(final UUID courseId, final AssignStudentToCourseRequest assignStudentToCourseRequest);

    void takeCourse(final UUID courseId);
}
