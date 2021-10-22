package com.goals.course.manager.service.implementation;

import com.goals.course.manager.CourseFilters;
import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.service.filtering.course.model.CourseFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository mockCourseRepository;

    @Mock
    private CourseMapper mockCourseMapper;

    @InjectMocks
    private CourseServiceImpl service;

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

        final var courseFilter = CourseFilter.builder().filter(CourseFilters.ALL).build();

        // WHEN
        service.getAllCourses(courseFilter);

        // THEN
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
        final var courseDTO1 = CourseDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var courseDTO2 = CourseDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000002")).build();
        final var courseDTO3 = CourseDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000003")).build();
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(courseDTO1, courseDTO2, courseDTO3);

        final var courseFilter = CourseFilter.builder().filter(CourseFilters.ALL).build();

        // WHEN
        final var result = service.getAllCourses(courseFilter);

        // THEN
        assertThat(result)
                .containsExactlyInAnyOrder(courseDTO1, courseDTO2, courseDTO3);
    }

}
