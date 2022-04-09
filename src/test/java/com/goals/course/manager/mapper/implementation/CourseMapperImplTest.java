package com.goals.course.manager.mapper.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.InstructorDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.mapper.InstructorMapper;
import com.goals.course.manager.mapper.LessonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseMapperImplTest {

    @Mock
    private InstructorMapper mockInstructorsMapper;

    @Mock
    private LessonMapper mockLessonMapper;

    @InjectMocks
    private CourseMapperImpl service;

    @BeforeEach
    public void before() {
        ReflectionTestUtils.setField(service, "averageGradeToPassACourse", 3);
    }

    @Test
    void mapToCourse_id_checkResult() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var courseDTO = CourseDTO.builder().id(courseId).build();

        // WHEN
        final var result = service.mapToCourse(courseDTO);

        // THEN
        assertThat(result.getId()).isSameAs(courseId);
    }

    @Test
    void mapToCourse_title_checkResult() {
        // GIVEN
        final var courseDTO = CourseDTO.builder().title("test title").build();

        // WHEN
        final var result = service.mapToCourse(courseDTO);

        // THEN
        assertThat(result.getTitle()).isSameAs("test title");
    }

    @Test
    void mapToCourse_instructors_callMapToInstructors() {
        // GIVEN
        final var instructors = Set.of(InstructorDTO.builder().build());
        final var courseDTO = CourseDTO.builder()
                .title("test title")
                .instructors(instructors)
                .build();

        // WHEN
        service.mapToCourse(courseDTO);

        // THEN
        verify(mockInstructorsMapper).mapToCourseInstructorSet(instructors);
    }

    @Test
    void mapToCourse_instructors_checkResult() {
        // GIVEN
        final var courseDTO = CourseDTO.builder()
                .title("test title")
                .instructors(Set.of(InstructorDTO.builder().build()))
                .build();

        final var instructors = Set.of(new CourseInstructor());
        when(mockInstructorsMapper.mapToCourseInstructorSet(any())).thenReturn(instructors);

        // WHEN
        final var result = service.mapToCourse(courseDTO);

        // THEN
        assertThat(result.getInstructors()).isSameAs(instructors);
    }

    @Test
    void mapToCourseDTO_id_checkResult() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var course = new Course().setId(courseId);

        // WHEN
        final var result = service.mapToCourseDTO(course);

        // THEN
        assertThat(result.getId()).isSameAs(courseId);
    }

    @Test
    void mapToCourseDTO_title_checkResult() {
        // GIVEN
        final var course = new Course().setTitle("test title");

        // WHEN
        final var result = service.mapToCourseDTO(course);

        // THEN
        assertThat(result.getTitle()).isSameAs("test title");
    }

    @Test
    void mapToCourseDTO_instructors_callMapToInstructorsDTOs() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var instructors = Set.of(new CourseInstructor().setId(instructorId));
        final var course = new Course().setInstructors(instructors);

        // WHEN
        service.mapToCourseDTO(course);

        // THEN
        verify(mockInstructorsMapper).mapToInstructorDTOSet(instructors);
    }

    @Test
    void mapToCourseDTO_instructors_checkResult() {
        // GIVEN
        final var instructor = InstructorDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var instructorDTOS = Set.of(instructor);
        when(mockInstructorsMapper.mapToInstructorDTOSet(any())).thenReturn(instructorDTOS);

        // WHEN
        final var result = service.mapToCourseDTO(new Course());

        // THEN
        assertThat(result.getInstructors()).isSameAs(instructorDTOS);
    }

    @Test
    void mapToCourseDTO_lessons_callMapToLessonDTO() {
        // GIVEN
        final var lesson1 = new Lesson().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var lesson2 = new Lesson().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var lesson3 = new Lesson().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var lessons = Set.of(lesson1, lesson2, lesson3);
        final var course = new Course().setLessons(lessons);

        // WHEN
        service.mapToCourseDTO(course);

        // THEN
        verify(mockLessonMapper, times(3)).mapToLessonDTO(any());
        verify(mockLessonMapper).mapToLessonDTO(lesson1);
        verify(mockLessonMapper).mapToLessonDTO(lesson2);
        verify(mockLessonMapper).mapToLessonDTO(lesson3);
    }

    @Test
    void mapToCourseDTO_lessons_checkResult() {
        // GIVEN
        final var lesson1 = LessonDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var lesson2 = LessonDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000002")).build();
        final var lesson3 = LessonDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000003")).build();
        when(mockLessonMapper.mapToLessonDTO(any())).thenReturn(lesson1, lesson2, lesson3);
        final var course = new Course().setLessons(Set.of(new Lesson().setTitle("1"), new Lesson().setTitle("2"), new Lesson().setTitle("3")));

        // WHEN
        final var result = service.mapToCourseDTO(course);

        // THEN
        assertThat(result.getLessons())
                .containsExactlyInAnyOrder(lesson1, lesson2, lesson3);
    }
}
