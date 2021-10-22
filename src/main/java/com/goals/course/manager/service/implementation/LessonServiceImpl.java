package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dao.repository.LessonRepository;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.exception.LessonNotFoundException;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.CourseService;
import com.goals.course.manager.service.LessonService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseService courseService;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public Lesson createLesson(final UUID courseId, final LessonDTO lessonDTO) {
        log.info("Creating course lesson {}", lessonDTO);
        final var course = courseService.getCourseById(courseId);
        final var lesson = lessonMapper.mapToLesson(lessonDTO);

        lesson.setCourse(course);

        return lessonRepository.save(lesson);
    }

    @Override
    public LessonDTO getLessonById(final UUID lessonId) {
        final var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new LessonNotFoundException("Lesson '%s' not found!".formatted(lessonId)));

        return lessonMapper.mapToLessonDTO(lesson);
    }

    @Override
    public List<LessonDTO> getLessonsByCourseId(final UUID courseId) {
        final var course = courseService.getCourseById(courseId);

        return lessonRepository.findAllByCourseId(course.getId())
                .stream()
                .map(lessonMapper::mapToLessonDTO)
                .toList();
    }
}
