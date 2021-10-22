package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.InstructorRepository;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.UserIsNotInstructorException;
import com.goals.course.manager.exception.UserNotFoundException;
import com.goals.course.manager.service.UserProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceImplTest {

    @Mock
    private UserProvider mockUserProvider;
    @Mock
    private InstructorRepository mockInstructorRepository;

    @InjectMocks
    private InstructorServiceImpl service;

    @Test
    void assignCourseInstructor_callFindUserById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        final UserDTO userDTO = buildUserDTO(Roles.INSTRUCTOR);
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(userDTO));

        // WHEN
        service.getOrCreateInstructorById(instructorId);

        // THEN
        verify(mockUserProvider).findUserById(instructorId);
    }

    @Test
    void assignCourseInstructor_callFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        final UserDTO userDTO = buildUserDTO(Roles.INSTRUCTOR);
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(userDTO));

        // WHEN
        service.getOrCreateInstructorById(instructorId);

        // THEN
        verify(mockInstructorRepository).findById(instructorId);
    }

    @Test
    void assignCourseInstructor_instructorNotFound_throwException() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.empty());

        // WHEN
        final var expectedException = assertThrows(
                UserNotFoundException.class,
                () -> service.getOrCreateInstructorById(instructorId));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Instructor with id '00000000-0000-0000-0000-000000000012' not found");
    }

    @Test
    void assignCourseInstructor_instructorNotFound_neverCallFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.empty());

        // WHEN
        assertThrows(UserNotFoundException.class, () -> service.getOrCreateInstructorById(instructorId));

        // THEN
        verify(mockInstructorRepository, never()).findById(any());
    }

    @Test
    void assignCourseInstructor_userIsNotInstructor_throwException() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        final UserDTO userDTO = buildUserDTO(Roles.STUDENT);
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(userDTO));

        // WHEN
        final var expectedException = assertThrows(
                UserIsNotInstructorException.class,
                () -> service.getOrCreateInstructorById(instructorId));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Cannot assign instructor to course. User with id '00000000-0000-0000-0000-000000000012' has no rights");
    }

    @Test
    void assignCourseInstructor_userIsNotInstructor_neverCallFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        final UserDTO userDTO = buildUserDTO(Roles.STUDENT);
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(userDTO));

        // WHEN
        assertThrows(UserIsNotInstructorException.class, () -> service.getOrCreateInstructorById(instructorId));

        // THEN
        verify(mockInstructorRepository, never()).findById(any());
    }

    @Test
    void assignCourseInstructor_userRolesIsEmpty_throwException() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        final UserDTO userDTO = buildUserDTO(Roles.STUDENT);
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(userDTO));

        // WHEN
        final var expectedException = assertThrows(
                UserIsNotInstructorException.class,
                () -> service.getOrCreateInstructorById(instructorId));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Cannot assign instructor to course. User with id '00000000-0000-0000-0000-000000000012' has no rights");
    }

    @Test
    void assignCourseInstructor_checkResult() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(buildUserDTO(Roles.INSTRUCTOR)));
        final var instructor = new Instructor().setId(instructorId);
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.of(instructor));

        // WHEN
        final var result = service.getOrCreateInstructorById(instructorId);

        // THEN
        assertThat(result).isSameAs(instructor);
        verify(mockUserProvider).findUserById(instructorId);
    }

    @Test
    void assignCourseInstructor_instructorNotFoundInRepo_checkResult() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockUserProvider.findUserById(any())).thenReturn(Optional.of(buildUserDTO(Roles.INSTRUCTOR)));
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        final var result = service.getOrCreateInstructorById(instructorId);

        // THEN
        final var instructor = new Instructor().setId(instructorId);
        assertThat(result).isEqualTo(instructor);
    }


    private UserDTO buildUserDTO(final Roles instructor) {
        final var userRole = RoleDTO.builder().title(instructor.name()).build();
        return UserDTO.builder().roles(List.of(userRole)).build();
    }

}
