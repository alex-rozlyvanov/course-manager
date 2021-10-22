package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.AssignInstructorRequest;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.*;
import com.goals.course.manager.service.filtering.course.CourseFilteringService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerImplTest {

    @Mock
    private StudentService mockStudentService;
    @Mock
    private CourseService mockCourseService;
    @Mock
    private CourseFilteringService mockCourseFilteringService;
    @Mock
    private CourseCreationService mockCourseCreationService;
    @Mock
    private CourseMapper mockCourseMapper;
    @Mock
    private CourseAssigmentService mockCourseAssigmentService;
    @Mock
    private LessonService mockLessonService;
    @Mock
    private LessonMapper mockLessonMapper;
    @InjectMocks
    private CourseControllerImpl service;

    @Test
    void createCourse_callCreateCourse() {
        // GIVEN
        final var courseDTO = CourseDTO.builder().title("test title").build();

        // WHEN
        service.createCourse(courseDTO);

        // THEN
        verify(mockCourseCreationService).createCourse(courseDTO);
    }

    @Test
    void createCourse_checkResult() {
        // GIVEN
        final var courseDTO = CourseDTO.builder().title("test title").build();

        final var savedCourseWithId = CourseDTO.builder()
                .title("saved course with id")
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .build();
        when(mockCourseCreationService.createCourse(any())).thenReturn(savedCourseWithId);

        // WHEN
        final var result = service.createCourse(courseDTO);

        // THEN
        assertThat(result).isSameAs(savedCourseWithId);
    }

    @Test
    void assignCourseInstructor_callAssignCourseInstructor() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var assignInstructorRequest = AssignInstructorRequest.builder()
                .instructorId(instructorId)
                .build();

        // WHEN
        service.assignCourseInstructor(courseId, assignInstructorRequest);

        // THEN
        verify(mockCourseAssigmentService).assignInstructorToCourse(courseId, instructorId);
    }

    @Test
    void getCourseById_callGetCourseById() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // WHEN
        service.getCourseById(courseId);

        // THEN
        verify(mockCourseService).getCourseById(courseId);
    }

    @Test
    void getCourseById_callMapToCourseDTO() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        final var course = new Course().setId(courseId);
        when(mockCourseService.getCourseById(any())).thenReturn(course);

        // WHEN
        service.getCourseById(courseId);

        // THEN
        verify(mockCourseMapper).mapToCourseDTO(course);
    }

    @Test
    void getCourseById_checkResult() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var courseDTO = CourseDTO.builder()
                .id(courseId)
                .build();
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(courseDTO);

        // WHEN
        final var result = service.getCourseById(null);

        // THEN
        assertThat(result).isSameAs(courseDTO);
    }

    @Test
    void getAllCourse_checkResult() {
        // GIVEN
        final var courseDTO1 = CourseDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var courseDTO2 = CourseDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var expectedList = List.of(courseDTO1, courseDTO2);
        when(mockCourseService.getAllCourses(any())).thenReturn(expectedList);

        // WHEN
        final var result = service.getAllCourse(null, mock(Authentication.class));

        // THEN
        assertThat(result).isSameAs(expectedList);
    }

    @Test
    void createCourseLesson_callCreateCourseLesson() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var lessonDTO = LessonDTO.builder()
                .id(lessonId)
                .build();

        // WHEN
        service.createCourseLesson(courseId, lessonDTO);

        // THEN
        verify(mockLessonService).createLesson(courseId, lessonDTO);
    }

    @Test
    void createCourseLesson_callMapToLessonDTO() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var createdLesson = new Lesson().setId(lessonId);
        when(mockLessonService.createLesson(any(), any())).thenReturn(createdLesson);


        // WHEN
        service.createCourseLesson(null, null);

        // THEN
        verify(mockLessonMapper).mapToLessonDTO(createdLesson);
    }

    @Test
    void createCourseLesson_checkResult() {
        // GIVEN
        final var lessonId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var lessonDTO = LessonDTO.builder()
                .id(lessonId)
                .build();
        when(mockLessonMapper.mapToLessonDTO(any())).thenReturn(lessonDTO);

        // WHEN
        final var result = service.createCourseLesson(null, null);

        // THEN
        assertThat(result).isSameAs(lessonDTO);
    }

}
