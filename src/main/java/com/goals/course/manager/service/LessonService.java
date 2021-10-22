package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.LessonDTO;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    Lesson createLesson(final UUID courseId, final LessonDTO lessonDTO);

    LessonDTO getLessonById(final UUID lessonId);

    List<LessonDTO> getLessonsByCourseId(final UUID courseId);
}
