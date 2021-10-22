package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.LessonDTO;

public interface LessonMapper {
    Lesson mapToLesson(final LessonDTO lessonDTO);

    LessonDTO mapToLessonDTO(final Lesson lesson);
}
