package com.goals.course.manager.service;

import com.goals.course.manager.dto.GradeDTO;

import java.util.Optional;
import java.util.UUID;

public interface GradeProvider {
    Optional<GradeDTO> findGradeByLessonIdAndStudentId(final UUID lessonId, final UUID studentId);
}
