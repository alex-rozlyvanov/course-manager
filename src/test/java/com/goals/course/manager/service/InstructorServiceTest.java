package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.InstructorRepository;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.UserIsNotInstructorException;
import com.goals.course.manager.exception.UserNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorServiceTest {

    @Mock
    private UserProvider mockUserProvider;
    @Mock
    private InstructorRepository mockInstructorRepository;

    @InjectMocks
    private InstructorService service;

    @Test
    void assignCourseInstructor_callFindUserById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var userDTO = buildUserDTO(Roles.INSTRUCTOR);
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));

        // WHEN
        service.getOrCreateInstructorById(instructorId);

        // THEN
        verify(mockUserProvider).findUserById(instructorId);
    }

    @Test
    void assignCourseInstructor_userIsInstructor_callFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var userDTO = buildUserDTO(instructorId, Roles.INSTRUCTOR);
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.of(new Instructor()));

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockInstructorRepository).findById(instructorId);
    }

    @Test
    void assignCourseInstructor_instructorNotFound_throwException() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(UserNotFoundException.class)
                        .hasMessage("Instructor with id '00000000-0000-0000-0000-000000000012' not found"));
    }

    @Test
    void assignCourseInstructor_instructorNotFound_neverCallFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono).verifyError();
        verify(mockInstructorRepository, never()).findById(any());
    }

    @Test
    void assignCourseInstructor_userIsNotInstructor_throwException() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        final var userDTO = buildUserDTO(instructorId, Roles.STUDENT);
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(UserIsNotInstructorException.class)
                        .hasMessage("Cannot assign instructor to course. User with id '00000000-0000-0000-0000-000000000012' has no rights"));
    }

    @Test
    void assignCourseInstructor_userIsNotInstructor_neverCallFindById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        final var userDTO = buildUserDTO(Roles.STUDENT);
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono).verifyError();
        verify(mockInstructorRepository, never()).findById(any());
    }

    @Test
    void assignCourseInstructor_checkResult() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildUserDTO(Roles.INSTRUCTOR)));
        final var instructor = new Instructor().setId(instructorId);
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.of(instructor));

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono)
                .expectNext(instructor)
                .verifyComplete();
    }

    @Test
    void assignCourseInstructor_instructorNotFoundInRepo_callSave() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var userDTO = buildUserDTO(instructorId, Roles.INSTRUCTOR);
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.empty());
        final var instructor = new Instructor().setId(instructorId);
        when(mockInstructorRepository.save(any())).thenReturn(instructor);

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono)
                .expectNext(instructor)
                .verifyComplete();
        verify(mockInstructorRepository).save(new Instructor().setId(instructorId));
    }

    @Test
    void assignCourseInstructor_instructorNotFoundInRepo_checkResult() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildUserDTO(Roles.INSTRUCTOR)));
        when(mockInstructorRepository.findById(any())).thenReturn(Optional.empty());
        final var instructor = new Instructor().setId(instructorId);
        when(mockInstructorRepository.save(any())).thenReturn(instructor);

        // WHEN
        final var mono = service.getOrCreateInstructorById(instructorId);

        // THEN
        StepVerifier.create(mono)
                .expectNext(instructor)
                .verifyComplete();
    }

    private UserDTO buildUserDTO(final Roles instructor) {
        final var userRole = RoleDTO.builder().title(instructor.name()).build();
        return UserDTO.builder().roles(List.of(userRole)).build();
    }

    private UserDTO buildUserDTO(final UUID id, final Roles instructor) {
        final var userRole = RoleDTO.builder().title(instructor.name()).build();
        return UserDTO.builder()
                .id(id)
                .roles(List.of(userRole))
                .build();
    }

}
