package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.enums.CourseFilters;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.service.CourseService;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;

    @Override
    public Course getCourseById(final UUID courseId) {
        log.info("Getting course by id '{}'", courseId);
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    @Override
    public List<CourseDTO> getAllCourses(final CourseFilter filter) {
        log.info("Getting all courses '{}'", filter);
        return queryByFilter(filter.getFilter())
                .apply(filter.getUserId())
                .stream()
                .map(courseMapper::mapToCourseDTO)
                .toList();
    }

    private Function<UUID, List<Course>> queryByFilter(final CourseFilters filter) {
        return switch (filter) {
            case ALL -> id -> courseRepository.findAll();
            case INSTRUCTOR -> courseRepository::findByInstructorId;
            case STUDENT -> courseRepository::findByStudentId;
        };
    }

}
