package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.enums.CourseFilters;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository mockCourseRepository;

    @Mock
    private CourseMapper mockCourseMapper;

    @InjectMocks
    private CourseService service;

    @Test
    void getCourseById_callFindById() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));

        // WHEN
        service.getCourseById(courseId);

        // THEN
        verify(mockCourseRepository).findById(courseId);
    }

    @Test
    void getCourseById_checkResult() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        final var course = new Course().setId(courseId);
        when(mockCourseRepository.findById(any())).thenReturn(of(course));

        // WHEN
        final var result = service.getCourseById(courseId);

        // THEN
        assertThat(result).isSameAs(course);
    }

    @Test
    void getCourseById_courseNotFound_throwException() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000004");
        when(mockCourseRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        final var expectedException = assertThrows(CourseNotFoundException.class,
                () -> service.getCourseById(courseId));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Course with id '00000000-0000-0000-0000-000000000004' not found");
    }

    @Test
    void getAllCourses_callMapToCourseDTO() {
        // GIVEN
        final var course1 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var course2 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var course3 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        when(mockCourseRepository.findAll()).thenReturn(List.of(course1, course2, course3));
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(Mono.just(CourseDTO.builder().build()));

        final var courseFilter = CourseFilter.builder().filter(CourseFilters.ALL).build();

        // WHEN
        final var mono = service.getAllCourses(courseFilter);

        // THEN
        StepVerifier.create(mono).expectNextCount(3).verifyComplete();

        verify(mockCourseMapper, times(3)).mapToCourseDTO(any());
        verify(mockCourseMapper).mapToCourseDTO(course1);
        verify(mockCourseMapper).mapToCourseDTO(course2);
        verify(mockCourseMapper).mapToCourseDTO(course3);
    }

    @Test
    void getAllCourses_checkResult() {
        // GIVEN
        final var course1 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var course2 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var course3 = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        when(mockCourseRepository.findAll()).thenReturn(List.of(course1, course2, course3));
        final var courseDTO1 = buildCourseDTO("00000000-0000-0000-0000-000000000001");
        final var courseDTO2 = buildCourseDTO("00000000-0000-0000-0000-000000000002");
        final var courseDTO3 = buildCourseDTO("00000000-0000-0000-0000-000000000003");
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(
                Mono.just(courseDTO1),
                Mono.just(courseDTO2),
                Mono.just(courseDTO3)
        );

        final var courseFilter = CourseFilter.builder().filter(CourseFilters.ALL).build();

        // WHEN
        final var flux = service.getAllCourses(courseFilter);

        // THEN
        StepVerifier.create(flux)
                .expectNext(courseDTO1)
                .expectNext(courseDTO2)
                .expectNext(courseDTO3)
                .verifyComplete();
    }

    private CourseDTO buildCourseDTO(final String id) {
        return CourseDTO.builder().id(UUID.fromString(id)).build();
    }

}
