package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dto.CourseDTO;

public interface CourseMapper {
    Course mapToCourse(final CourseDTO courseDTO);

    CourseDTO mapToCourseDTO(final Course course);
}
