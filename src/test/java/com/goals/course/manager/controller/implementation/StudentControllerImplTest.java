package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.StudentService;
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
class StudentControllerImplTest {

    @Mock
    private StudentService mockStudentService;

    @InjectMocks
    private StudentControllerImpl service;

    @Test
    void getStudentById_callGetStudentById() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // WHEN
        service.getStudentById(studentId);

        // THEN
        verify(mockStudentService).getStudentById(studentId);
    }

    @Test
    void getStudentById_checkResult() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var userDTO = UserDTO.builder().id(studentId).build();
        when(mockStudentService.getStudentById(any())).thenReturn(userDTO);

        // WHEN
        final var result = service.getStudentById(null);

        // THEN
        assertThat(result).isSameAs(userDTO);
    }
}
