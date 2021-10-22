package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.dto.AssignStudentToCourseRequest;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.CourseAssigmentService;
import com.goals.course.manager.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseAssigmentControllerImplTest {
    @Mock
    private SecurityService mockSecurityService;
    @Mock
    private CourseAssigmentService mockCourseAssigmentService;
    @InjectMocks
    private CourseAssigmentControllerImpl service;

    @Test
    void assignUserToCourse_callTakeCourse() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var assignStudentToCourseRequest = new AssignStudentToCourseRequest(userId);

        // WHEN
        service.assignUserToCourse(courseId, assignStudentToCourseRequest);

        // THEN
        verify(mockCourseAssigmentService).assignStudentToCourse(courseId, userId);
    }

    @Test
    void takeCourse_checkResult() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockSecurityService.getCurrentUser()).thenReturn(UserDTO.builder().id(userId).build());

        // WHEN
        service.takeCourse(courseId);

        // THEN
        verify(mockCourseAssigmentService).assignStudentToCourse(courseId, userId);
    }

}
