package com.goals.course.manager.controller;

import com.goals.course.manager.dto.LessonDTO;

import java.util.UUID;

public interface LessonController {
    LessonDTO getLessonById(final UUID lessonId);
}
