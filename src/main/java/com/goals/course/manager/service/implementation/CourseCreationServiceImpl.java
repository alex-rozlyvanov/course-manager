package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.exception.CourseCannotBeCreatedException;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.service.CourseAssigmentService;
import com.goals.course.manager.service.CourseCreationService;
import com.goals.course.manager.service.LessonService;
import com.goals.course.manager.service.validation.CourseCreationValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CourseCreationServiceImpl implements CourseCreationService {
    private final CourseMapper courseMapper;
    private final CourseRepository courseRepository;
    private final CourseCreationValidation courseCreationValidation;
    private final CourseAssigmentService courseAssigmentService;
    private final LessonService lessonService;

    @Override
    @Transactional
    public CourseDTO createCourse(final CourseDTO courseDTO) {
        log.info("Creating course {}", courseDTO);
        validation(courseDTO);
        final var mappedCourse = courseMapper.mapToCourse(courseDTO);
        final var savedCourse = courseRepository.save(mappedCourse);

        assignInstructors(savedCourse);
        savedCourse.setLessons(createLessons(savedCourse, courseDTO.getLessons()));

        log.info("Created course {}", savedCourse);
        return courseMapper.mapToCourseDTO(savedCourse);
    }

    private Set<Lesson> createLessons(final Course course, final Set<LessonDTO> lessons) {
        return lessons
                .stream()
                .map(lessonDTO -> lessonService.createLesson(course.getId(), lessonDTO))
                .collect(Collectors.toSet());
    }

    private void assignInstructors(final Course savedCourse) {
        log.info("Before {}", savedCourse);
        savedCourse
                .getInstructors()
                .forEach(i -> courseAssigmentService.assignInstructorToCourse(savedCourse.getId(), i.getInstructor().getId()));
        log.info("After {}", savedCourse);
    }

    private void validation(final CourseDTO courseDTO) {
        final var validationResult = courseCreationValidation.validateCreation(courseDTO);

        if (validationResult.isNotValid()) {
            throw new CourseCannotBeCreatedException(validationResult.getMessage());
        }
    }

}
