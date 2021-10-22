package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.service.LessonService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonControllerImplTest {

    @Mock
    private LessonService mockLessonService;

    @InjectMocks
    private LessonControllerImpl service;

    @Test
    void getLessonById_callGetLessonById() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // WHEN
        service.getLessonById(lessonId);

        // THEN
        verify(mockLessonService).getLessonById(lessonId);
    }

    @Test
    void getLessonById_checkResult() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var lessonDTO = LessonDTO.builder().id(lessonId).build();
        when(mockLessonService.getLessonById(any())).thenReturn(lessonDTO);

        // WHEN
        final var result = service.getLessonById(null);

        // THEN
        assertThat(result).isSameAs(lessonDTO);
    }
}
