package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.GradeProvider;
import com.goals.course.manager.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonMapperTest {

    @Mock
    private SecurityService mockSecurityService;
    @Mock
    private GradeProvider mockGradeProvider; // TODO impl

    @InjectMocks
    private LessonMapper service;

    @Test
    void mapToLesson_id_checkResult() {
        // GIVEN
        final var lessonDTO = LessonDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .build();

        // WHEN
        final var result = service.mapToLesson(lessonDTO);

        // THEN
        assertThat(result.getId()).hasToString("00000000-0000-0000-0000-000000000001");
    }

    @Test
    void mapToLesson_title_checkResult() {
        // GIVEN
        final var lessonDTO = LessonDTO.builder()
                .title("test title")
                .build();

        // WHEN
        final var result = service.mapToLesson(lessonDTO);

        // THEN
        assertThat(result.getTitle()).isEqualTo("test title");
    }

    @Test
    void mapToLessonDTO_id_checkResult() {
        // GIVEN
        final var lesson = new Lesson()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(mock(UserDTO.class)));

        // WHEN
        final var mono = service.mapToLessonDTO(lesson);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getId()).hasToString("00000000-0000-0000-0000-000000000001"))
                .verifyComplete();
    }

    @Test
    void mapToLessonDTO_title_checkResult() {
        // GIVEN
        final var lesson = new Lesson().setTitle("test title");

        // WHEN
        final var mono = service.mapToLessonDTO(lesson);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result.getTitle()).isEqualTo("test title"))
                .verifyComplete();

    }
}
