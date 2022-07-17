package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.StudentRepository;
import com.goals.course.manager.dto.RoleDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.StudentNotFoundException;
import com.goals.course.manager.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository mockStudentRepository;
    @Mock
    private UserProvider mockUserProvider;

    @InjectMocks
    private StudentService service;

    @Test
    void getOrCreateStudent_callFindUserById() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildStudentUserDTO()));

        // WHEN
        service.getOrCreateStudent(userId);

        // THEN
        verify(mockUserProvider).findUserById(userId);
    }

    @Test
    void getOrCreateStudent_userNotFound_throwException() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(UserNotFoundException.class)
                        .hasMessage("Student with id '00000000-0000-0000-0000-000000000001' not found"));
    }

    @Test
    void getOrCreateStudent_userIsNotStudent_throwException() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildUserDTO(Roles.INSTRUCTOR)));

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("User is not a student!"));
    }

    @Test
    void getOrCreateStudent_callStudentRepositoryFindById() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildStudentUserDTO(userId)));
        when(mockStudentRepository.findById(any())).thenReturn(of(new Student()));

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockStudentRepository).findById(userId);
    }

    @Test
    void getOrCreateStudent_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildStudentUserDTO(userId)));
        final var student = new Student().setId(userId);
        when(mockStudentRepository.findById(any())).thenReturn(of(student));

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono)
                .expectNext(student)
                .verifyComplete();
    }

    @Test
    void getOrCreateStudent_studentNotFound_callStudentRepositorySave() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildStudentUserDTO(userId)));
        when(mockStudentRepository.findById(any())).thenReturn(empty());
        final var createdStudent = new Student();
        when(mockStudentRepository.save(any())).thenReturn(createdStudent);

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        final var captor = ArgumentCaptor.forClass(Student.class);
        verify(mockStudentRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(userId);
    }

    @Test
    void getOrCreateStudent_studentNotFound_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(buildStudentUserDTO(userId)));
        when(mockStudentRepository.findById(any())).thenReturn(empty());
        final var createdStudent = new Student().setId(userId);
        when(mockStudentRepository.save(any())).thenReturn(createdStudent);

        // WHEN
        final var mono = service.getOrCreateStudent(userId);

        // THEN
        StepVerifier.create(mono)
                .expectNext(createdStudent)
                .verifyComplete();
    }

    private UserDTO buildStudentUserDTO() {
        return buildUserDTO(Roles.STUDENT, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    private UserDTO buildUserDTO(final Roles role) {
        return buildUserDTO(role, UUID.fromString("00000000-0000-0000-0000-000000000000"));
    }

    private UserDTO buildStudentUserDTO(final UUID userID) {
        return buildUserDTO(Roles.STUDENT, userID);
    }

    private UserDTO buildUserDTO(final Roles role, final UUID userID) {
        final var roleDTO = RoleDTO.builder().title(role.name()).build();
        return UserDTO.builder()
                .id(userID)
                .roles(List.of(roleDTO))
                .build();
    }

    @Test
    void getStudentById_callFindById() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        when(mockStudentRepository.findById(any())).thenReturn(Optional.of(new Student()));

        // WHEN
        service.getStudentById(studentId);

        // THEN
        verify(mockStudentRepository).findById(studentId);
    }

    @Test
    void getStudentById_studentNotFound_throwException() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        when(mockStudentRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        final var mono = service.getStudentById(studentId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(StudentNotFoundException.class)
                        .hasMessage("Student '00000000-0000-0000-0000-000000000003' not found!"));
    }

    @Test
    void getStudentById_callFindUserById() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var student = new Student().setId(studentId);
        when(mockStudentRepository.findById(any())).thenReturn(Optional.of(student));
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(UserDTO.builder().build()));

        // WHEN
        final var mono = service.getStudentById(null);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockUserProvider).findUserById(studentId);
    }

    @Test
    void getStudentById_checkResult() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var student = new Student().setId(studentId);
        when(mockStudentRepository.findById(any())).thenReturn(Optional.of(student));
        final var userDTO = UserDTO.builder().id(studentId).build();
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.just(userDTO));

        // WHEN
        final var mono = service.getStudentById(null);

        // THEN
        StepVerifier.create(mono)
                .expectNext(userDTO)
                .verifyComplete();
    }

    @Test
    void getStudentById_userNotFound_throwException() {
        // GIVEN
        final var studentId = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var student = new Student().setId(studentId);
        when(mockStudentRepository.findById(any())).thenReturn(Optional.of(student));
        when(mockUserProvider.findUserById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.getStudentById(studentId);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(UserNotFoundException.class)
                        .hasMessage("User '00000000-0000-0000-0000-000000000003' not found!"));
    }
}
