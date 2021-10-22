package com.goals.course.manager.service.filtering.course.model;

import com.goals.course.manager.CourseFilters;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@Builder
public class CourseFilter {
    private final CourseFilters filter;
    private final UUID userId;
}
