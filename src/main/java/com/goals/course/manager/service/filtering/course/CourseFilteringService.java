package com.goals.course.manager.service.filtering.course;

import com.goals.course.manager.enums.CourseFilters;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import org.springframework.security.core.Authentication;

public interface CourseFilteringService {
    CourseFilter getCourseFilter(final CourseFilters filter, final Authentication authentication);
}
