package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dao.repository.LessonRepository;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.exception.LessonNotFoundException;
import com.goals.course.manager.mapper.LessonMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class LessonService {
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final LessonMapper lessonMapper;

    @Transactional
    public Lesson createLesson(final UUID courseId, final LessonDTO lessonDTO) {
        log.info("Creating course lesson {}", lessonDTO);
        final var course = courseService.getCourseById(courseId);
        final var lesson = lessonMapper.mapToLesson(lessonDTO);

        lesson.setCourse(course);

        return lessonRepository.save(lesson);
    }

    public Mono<LessonDTO> getLessonById(final UUID lessonId) {
        final var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson '%s' not found!".formatted(lessonId)));

        return lessonMapper.mapToLessonDTO(lesson);
    }

    public Flux<LessonDTO> getLessonsByCourseId(final UUID courseId) {
        final var course = courseService.getCourseById(courseId);

        return Flux.fromIterable(lessonRepository.findAllByCourseId(course.getId()))
                .flatMap(lessonMapper::mapToLessonDTO);
    }
}
