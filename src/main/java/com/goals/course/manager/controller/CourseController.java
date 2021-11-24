package com.goals.course.manager.controller;

import com.goals.course.manager.dto.AssignInstructorRequest;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.enums.CourseFilters;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

public interface CourseController {
    List<CourseDTO> getAllCourse(final CourseFilters filter, final Authentication authentication);

    CourseDTO createCourse(final CourseDTO courseDTO);

    void assignCourseInstructor(final UUID courseId, final AssignInstructorRequest instructorRequest);

    CourseDTO getCourseById(final UUID courseId);

    LessonDTO createCourseLesson(final UUID courseId, final LessonDTO lessonDTO);

    List<LessonDTO> getCourseLessons(final UUID courseId);

    List<UserDTO> getCourseStudents(final UUID courseId);
}
