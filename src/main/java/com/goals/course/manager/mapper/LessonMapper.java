package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.GradeDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.service.GradeProvider;
import com.goals.course.manager.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class LessonMapper {
    private final SecurityService securityService;
    private final GradeProvider gradeProvider;

    public Lesson mapToLesson(final LessonDTO lessonDTO) {
        return new Lesson()
                .setId(lessonDTO.getId())
                .setTitle(lessonDTO.getTitle());
    }

    public Mono<LessonDTO> mapToLessonDTO(final Lesson lesson) {
        final var lessonDTOBuilder = LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle());

        return getGradeDTO(lesson)
                .map(gradeDTO -> lessonDTOBuilder.gradeDTO(gradeDTO).build())
                .switchIfEmpty(Mono.fromSupplier(lessonDTOBuilder::build));
    }

    private Mono<GradeDTO> getGradeDTO(final Lesson lesson) {
        if (nonNull(lesson.getId())) {
            return securityService.getCurrentUser()
                    .filter(currentUser -> currentUser.isStudent() && currentUser.isNotAdmin())
                    .flatMap(currentUser -> gradeProvider.findGradeByLessonIdAndStudentId(lesson.getId(), currentUser.getId()))
                    .switchIfEmpty(Mono.empty());
        }

        return Mono.empty();
    }
}
