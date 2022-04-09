package com.goals.course.manager.mapper.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.mapper.InstructorMapper;
import com.goals.course.manager.mapper.LessonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CourseMapperImpl implements CourseMapper {

    private final InstructorMapper instructorsMapper;
    private final LessonMapper lessonMapper;
    @Value("${app.course.averageGradeToPassACourse:80}")
    private Integer averageGradeToPassACourse;

    @Override
    public Course mapToCourse(final CourseDTO courseDTO) {
        final var instructors = instructorsMapper.mapToCourseInstructorSet(courseDTO.getInstructors());

        return new Course()
                .setId(courseDTO.getId())
                .setTitle(courseDTO.getTitle())
                .setInstructors(instructors);
    }

    @Override
    public CourseDTO mapToCourseDTO(final Course course) {
        final var instructors = instructorsMapper.mapToInstructorDTOSet(course.getInstructors());
        final var lessons = mapLessons(course);
        final var averageGrade = getAverageGrade(lessons);

        return CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .instructors(instructors)
                .lessons(lessons)
                .averageGrade(averageGrade)
                .coursePassed(isCoursePassed(averageGrade))
                .build();
    }

    private Double getAverageGrade(final Set<LessonDTO> lessons) {
        return lessons.stream()
                .filter(Objects::nonNull)
                .map(LessonDTO::getGradeDTO)
                .filter(Objects::nonNull)
                .mapToInt(gradeDTO -> nonNull(gradeDTO.getGrade()) ? gradeDTO.getGrade() : 0)
                .average()
                .orElse(0);
    }

    private boolean isCoursePassed(final Double averageGrade) {
        return averageGrade >= averageGradeToPassACourse;
    }

    private Set<LessonDTO> mapLessons(final Course course) {
        return course.getLessons()
                .stream()
                .map(lessonMapper::mapToLessonDTO)
                .collect(Collectors.toSet());
    }
}
