package com.goals.course.manager.controller;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.Lesson;
import com.goals.course.manager.dto.AssignInstructorRequest;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.enums.CourseFilters;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.*;
import com.goals.course.manager.service.filtering.course.CourseFilteringService;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

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
    private CourseController service;

    @Test
    void createCourse_callCreateCourse() {
        // GIVEN
        final var courseDTO = CourseDTO.builder().title("test title").build();
        when(mockCourseCreationService.createCourse(any())).thenReturn(Mono.just(CourseDTO.builder().build()));

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
        when(mockCourseCreationService.createCourse(any())).thenReturn(Mono.just(savedCourseWithId));

        // WHEN
        final var mono = service.createCourse(courseDTO);

        // THEN
        StepVerifier.create(mono)
                .expectNext(savedCourseWithId)
                .verifyComplete();
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
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(Mono.just(courseDTO));

        // WHEN
        final var mono = service.getCourseById(null);

        // THEN
        StepVerifier.create(mono)
                .expectNext(courseDTO)
                .verifyComplete();
    }

    @Test
    void getAllCourse_callGetCourseFilter() {
        // GIVEN
        final var courseDTO1 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        final var courseDTO2 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        when(mockCourseService.getAllCourses(any())).thenReturn(Flux.just(courseDTO1, courseDTO2));
        final var authentication = mock(Authentication.class);

        // WHEN
        service.getAllCourse(CourseFilters.ALL, authentication);

        // THEN
        verify(mockCourseFilteringService).getCourseFilter(CourseFilters.ALL, authentication);
    }

    @Test
    void getAllCourse_callGetAllCourses() {
        // GIVEN
        final var courseFilter = buildCourseFilter("00000000-0000-0000-0000-000000001111");
        when(mockCourseFilteringService.getCourseFilter(any(), any())).thenReturn(courseFilter);

        final var courseDTO1 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        final var courseDTO2 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        final var expectedList = List.of(courseDTO1, courseDTO2);
        when(mockCourseService.getAllCourses(any())).thenReturn(Flux.fromIterable(expectedList));

        // WHEN
        service.getAllCourse(null, mock(Authentication.class));

        // THEN
        verify(mockCourseService).getAllCourses(courseFilter);
    }

    @Test
    void getAllCourse_checkResult() {
        // GIVEN
        final var courseDTO1 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        final var courseDTO2 = buildCourseDTO("00000000-0000-0000-0000-000000000002");
        final var expectedList = List.of(courseDTO1, courseDTO2);
        when(mockCourseService.getAllCourses(any())).thenReturn(Flux.fromIterable(expectedList));

        // WHEN
        final var flux = service.getAllCourse(null, mock(Authentication.class));

        // THEN
        StepVerifier.create(flux)
                .expectNext(courseDTO1)
                .expectNext(courseDTO2)
                .verifyComplete();
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
        when(mockLessonMapper.mapToLessonDTO(any())).thenReturn(Mono.just(lessonDTO));

        // WHEN
        final var mono = service.createCourseLesson(null, null);

        // THEN
        StepVerifier.create(mono)
                .expectNext(lessonDTO)
                .verifyComplete();
    }

    @Test
    void getCourseStudents_call() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockStudentService.getStudentsByCourseId(any())).thenReturn(Flux.empty());

        // WHEN
        final var flux = service.getCourseStudents(courseId);

        // THEN
        StepVerifier.create(flux).verifyComplete();
        verify(mockStudentService).getStudentsByCourseId(courseId);
    }

    @Test
    void getCourseStudents_checkResult() {
        // GIVEN
        final var userDTO1 = buildUserDTO("00000000-0000-0000-0000-000000000001");
        final var userDTO2 = buildUserDTO("00000000-0000-0000-0000-000000000002");
        when(mockStudentService.getStudentsByCourseId(any())).thenReturn(Flux.just(userDTO1, userDTO2));

        // WHEN
        final var flux = service.getCourseStudents(null);

        // THEN
        StepVerifier.create(flux)
                .expectNext(userDTO1)
                .expectNext(userDTO2)
                .verifyComplete();
    }

    private UserDTO buildUserDTO(String userId) {
        return UserDTO.builder().id(UUID.fromString(userId)).build();
    }

    private CourseDTO buildCourseDTO(final String uuidId) {
        return CourseDTO.builder().id(UUID.fromString(uuidId)).build();
    }

    private CourseFilter buildCourseFilter(final String userUuidId) {
        return CourseFilter.builder()
                .userId(UUID.fromString(userUuidId))
                .build();
    }

}
