package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;

import java.util.List;
import java.util.UUID;

public interface CourseService {
    Course getCourseById(final UUID courseId);

    List<CourseDTO> getAllCourses(final CourseFilter filter);
}
