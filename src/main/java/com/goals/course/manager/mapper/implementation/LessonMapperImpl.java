package com.goals.course.manager.mapper.implementation;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.GradeDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.GradeProvider;
import com.goals.course.manager.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class LessonMapperImpl implements LessonMapper {
    private final SecurityService securityService;
    private final GradeProvider gradeProvider;

    @Override
    public Lesson mapToLesson(final LessonDTO lessonDTO) {
        return new Lesson()
                .setId(lessonDTO.getId())
                .setTitle(lessonDTO.getTitle());
    }

    @Override
    public LessonDTO mapToLessonDTO(final Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .gradeDTO(getGradeDTO(lesson))
                .build();
    }

    private GradeDTO getGradeDTO(final Lesson lesson) {
        if (nonNull(lesson.getId())) {
            final var currentUser = securityService.getCurrentUser();

            if (currentUser.isStudent() && currentUser.isNotAdmin()) {
                return gradeProvider.findGradeByLessonIdAndStudentId(lesson.getId(), currentUser.getId())
                        .orElseGet(() -> GradeDTO.builder().build());
            }
        }
        return GradeDTO.builder().build();
    }
}
