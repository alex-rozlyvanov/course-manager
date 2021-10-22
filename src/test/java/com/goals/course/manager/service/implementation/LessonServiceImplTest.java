package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dao.repository.LessonRepository;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.exception.LessonNotFoundException;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.CourseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceImplTest {

    @Mock
    private LessonRepository mockLessonRepository;
    @Mock
    private CourseService mockCourseService;
    @Mock
    private LessonMapper mockLessonMapper;
    @InjectMocks
    private LessonServiceImpl service;

    @Test
    void createLesson_callFindById() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockLessonMapper.mapToLesson(any())).thenReturn(new Lesson());


        // WHEN
        service.createLesson(courseId, LessonDTO.builder().build());

        // THEN
        verify(mockCourseService).getCourseById(courseId);
    }

    @Test
    void createLesson_callMapToLesson() {
        // GIVEN
        final var lessonDTO = LessonDTO.builder().title("test lesson title").build();
        when(mockLessonMapper.mapToLesson(any())).thenReturn(new Lesson());

        // WHEN
        service.createLesson(null, lessonDTO);

        // THEN
        verify(mockLessonMapper).mapToLesson(lessonDTO);
    }

    @Test
    void createLesson_callSave() {
        // GIVEN
        final var existingLesson = createLesson(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var newLesson = createLesson(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var course = buildCourse(existingLesson);
        when(mockCourseService.getCourseById(any())).thenReturn(course);
        when(mockLessonMapper.mapToLesson(any())).thenReturn(newLesson);

        // WHEN
        service.createLesson(null, null);

        // THEN
        final var captor = ArgumentCaptor.forClass(Lesson.class);
        verify(mockLessonRepository).save(captor.capture());
        assertThat(captor.getValue().getCourse())
                .isSameAs(course);
    }

    private Course buildCourse(final Lesson existingLesson) {
        final var lessons = new HashSet<Lesson>();
        lessons.add(existingLesson);

        return new Course()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setLessons(lessons);
    }

    private Lesson createLesson(final UUID lessonId) {
        return new Lesson().setId(lessonId);
    }

    @Test
    void createLesson_checkResult() {
        // GIVEN
        when(mockLessonMapper.mapToLesson(any())).thenReturn(new Lesson());
        final var savedLesson = new Lesson().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        when(mockLessonRepository.save(any())).thenReturn(savedLesson);

        // WHEN
        final var result = service.createLesson(null, null);

        // THEN
        assertThat(result).isSameAs(savedLesson);
    }

    @Test
    void getLessonById_callFindById() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockLessonRepository.findById(any())).thenReturn(Optional.of(new Lesson()));

        // WHEN
        service.getLessonById(lessonId);

        // THEN
        verify(mockLessonRepository).findById(lessonId);
    }

    @Test
    void getLessonById_lessonNotFound_throwException() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // WHEN
        final var expectedException = assertThrows(
                LessonNotFoundException.class,
                () -> service.getLessonById(lessonId)
        );

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Lesson '00000000-0000-0000-0000-000000000001' not found!");
    }

    @Test
    void getLessonById_callMapToLessonDTO() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var lesson = new Lesson().setId(lessonId);
        when(mockLessonRepository.findById(any())).thenReturn(Optional.of(lesson));

        // WHEN
        service.getLessonById(null);

        // THEN
        verify(mockLessonMapper).mapToLessonDTO(lesson);
    }

    @Test
    void getLessonById_checkResult() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockLessonRepository.findById(any())).thenReturn(Optional.of(new Lesson()));
        final var lessonDTO = LessonDTO.builder().id(lessonId).build();
        when(mockLessonMapper.mapToLessonDTO(any())).thenReturn(lessonDTO);

        // WHEN
        final var result = service.getLessonById(null);

        // THEN
        assertThat(result).isSameAs(lessonDTO);
    }
}
