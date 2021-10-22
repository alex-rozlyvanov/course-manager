package com.goals.course.manager.service;

import com.goals.course.manager.dto.CourseDTO;

public interface CourseCreationService {
    CourseDTO createCourse(final CourseDTO courseDTO);
}
