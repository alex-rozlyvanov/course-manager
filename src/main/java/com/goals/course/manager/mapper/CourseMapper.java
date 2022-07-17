package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class CourseMapper {

    private final InstructorMapper instructorsMapper;
    private final LessonMapper lessonMapper;
    @Value("${app.course.averageGradeToPassACourse:80}")
    private Integer averageGradeToPassACourse;

    public Course mapToCourse(final CourseDTO courseDTO) {
        final var instructors = instructorsMapper.mapToCourseInstructorSet(courseDTO.getInstructors());

        return new Course()
                .setId(courseDTO.getId())
                .setTitle(courseDTO.getTitle())
                .setInstructors(instructors);
    }

    public Mono<CourseDTO> mapToCourseDTO(final Course course) {
        final var instructors = instructorsMapper.mapToInstructorDTOSet(course.getInstructors());
        final var courseDTOBuilder = CourseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .instructors(instructors);

        return mapLessons(course)
                .map(lessons -> {
                    final var averageGrade = getAverageGrade(lessons);

                    return courseDTOBuilder
                            .averageGrade(averageGrade)
                            .coursePassed(isCoursePassed(averageGrade))
                            .lessons(lessons)
                            .build();
                });
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

    private Mono<Set<LessonDTO>> mapLessons(final Course course) {
        return Flux.fromIterable(course.getLessons())
                .flatMap(lessonMapper::mapToLessonDTO)
                .collect(Collectors.toSet());
    }
}
