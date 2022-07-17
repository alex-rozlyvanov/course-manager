package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.*;
import com.goals.course.manager.dao.repository.CourseInstructorRepository;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dao.repository.CourseStudentRepository;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.service.kafka.CourseAssignmentEventProducer;
import com.goals.course.manager.service.validation.CourseAssignmentValidation;
import com.goals.course.manager.service.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseAssigmentServiceTest {

    @Mock
    private StudentService mockStudentService;
    @Mock
    private CourseRepository mockCourseRepository;
    @Mock
    private CourseInstructorRepository mockCourseInstructorRepository;
    @Mock
    private CourseStudentRepository mockCourseStudentRepository;
    @Mock
    private CourseAssignmentValidation mockCourseAssignmentValidation;
    @Mock
    private InstructorService mockInstructorService;
    @Mock
    private CourseAssignmentEventProducer mockCourseAssignmentEventProducer;
    @InjectMocks
    private CourseAssigmentService service;

    @Test
    void assignStudentToCourse_callFindById() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(new Student()));
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(ValidationResult.valid());

        // WHEN
        final var mono = service.assignStudentToCourse(courseId, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseRepository).findById(courseId);
    }

    @Test
    void assignStudentToCourse_callGetOrCreateStudent() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(new Student()));
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(ValidationResult.valid());

        // WHEN
        final var mono = service.assignStudentToCourse(null, userId);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockStudentService).getOrCreateStudent(userId);
    }

    @Test
    void assignStudentToCourse_callCanStudentTakeACourse() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000002");

        final var course = new Course().setId(courseId);
        when(mockCourseRepository.findById(any())).thenReturn(of(course));

        final var student = new Student().setId(userId);
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(student));
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(ValidationResult.valid());

        // WHEN
        final var mono = service.assignStudentToCourse(courseId, userId);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseAssignmentValidation).canStudentTakeACourse(student);
    }

    @Test
    void assignStudentToCourse_callSave() {
        // GIVEN
        final var course = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockCourseRepository.findById(any())).thenReturn(of(course));

        final var student = new Student().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(student));
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(ValidationResult.valid());

        // WHEN
        final var mono = service.assignStudentToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        final var captor = ArgumentCaptor.forClass(CourseStudent.class);
        verify(mockCourseStudentRepository).save(captor.capture());
        assertCourseAssignment(captor.getValue(), course, student);
    }

    private void assertCourseAssignment(final CourseStudent courseStudent,
                                        final Course course,
                                        final Student student) {
        assertThat(courseStudent.getCourse()).isSameAs(course);
        assertThat(courseStudent.getStudent()).isSameAs(student);
        assertThat(courseStudent.getCourseIsCompleted()).isFalse();
    }

    @Test
    void assignStudentToCourse_callGenerateEventAsync() {
        // GIVEN
        final var course = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockCourseRepository.findById(any())).thenReturn(of(course));

        final var student = new Student().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(student));
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(ValidationResult.valid());

        final var courseStudent = new CourseStudent().setId(UUID.fromString("00000000-0000-0000-0000-000000000027"));
        when(mockCourseStudentRepository.save(any())).thenReturn(courseStudent);

        // WHEN
        final var mono = service.assignStudentToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseAssignmentEventProducer).generateStudentAssignmentEventAsync(courseStudent);
    }

    @Test
    void assignStudentToCourse_studentIsAlreadyAssignedToTheCourse_neverCallSave() {
        // GIVEN
        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(new Student()));
        when(mockCourseStudentRepository.findByCourseIdAndStudentId(any(), any())).thenReturn(new CourseStudent());

        // WHEN
        final var mono = service.assignStudentToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseStudentRepository, never()).save(any());
    }

    @Test
    void assignStudentToCourse_courseNotFound_throwException() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockCourseRepository.findById(any())).thenReturn(empty());

        // WHEN
        final var expectedException = assertThrows(
                CourseNotFoundException.class, () -> service.assignStudentToCourse(courseId, null));

        // THEN
        assertThat(expectedException.getMessage())
                .isEqualTo("Course with id '00000000-0000-0000-0000-000000000002' not found");
    }

    @Test
    void assignStudentToCourse_canStudentTakeACourseIsFalse_throwException() {
        // GIVEN
        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));
        when(mockStudentService.getOrCreateStudent(any())).thenReturn(Mono.just(new Student()));
        when(mockCourseStudentRepository.findByCourseIdAndStudentId(any(), any())).thenReturn(null);

        final var validationResult = ValidationResult.invalid("Student cannot take more then 3 courses");
        when(mockCourseAssignmentValidation.canStudentTakeACourse(any())).thenReturn(validationResult);

        // WHEN
        final var mono = service.assignStudentToCourse(null, null);

        // THEN
        StepVerifier.create(mono)
                .verifyErrorSatisfies(expectedException -> assertThat(expectedException)
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("Student cannot take more then 3 courses"));
        verify(mockCourseRepository, never()).save(any());
    }

    @Test
    void assignInstructorToCourse_callFindById() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        when(mockCourseRepository.findById(any())).thenReturn(of(buildCourse()));
        when(mockInstructorService.getOrCreateInstructorById(any())).thenReturn(Mono.just(new Instructor()));

        // WHEN
        service.assignInstructorToCourse(courseId, null);

        // THEN
        verify(mockCourseRepository).findById(courseId);
    }

    @Test
    void assignInstructorToCourse_courseNotFound_throwException() {
        // GIVEN
        final var courseId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // WHEN
        final var expectedException = assertThrows(
                CourseNotFoundException.class,
                () -> service.assignInstructorToCourse(courseId, null));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("Course with id '00000000-0000-0000-0000-000000000001' not found");
        verify(mockCourseRepository).findById(courseId);
    }

    @Test
    void assignInstructorToCourse_callGetOrCreateInstructorById() {
        // GIVEN
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(mockCourseRepository.findById(any())).thenReturn(of(buildCourse()));
        when(mockInstructorService.getOrCreateInstructorById(any())).thenReturn(Mono.just(new Instructor()));

        // WHEN
        service.assignInstructorToCourse(null, instructorId);

        // THEN
        verify(mockInstructorService).getOrCreateInstructorById(instructorId);
    }

    @Test
    void assignInstructorToCourse_callSave() {
        // GIVEN
        final var course = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockCourseRepository.findById(any())).thenReturn(of(course));
        final var newInstructor = buildInstructor();
        when(mockInstructorService.getOrCreateInstructorById(any())).thenReturn(Mono.just(newInstructor));

        // WHEN
        final var mono = service.assignInstructorToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();

        final var captor = ArgumentCaptor.forClass(CourseInstructor.class);
        verify(mockCourseInstructorRepository).save(captor.capture());

        final var actual = captor.getValue();
        assertThat(actual.getCourse()).isSameAs(course);
        assertThat(actual.getInstructor()).isSameAs(newInstructor);
    }

    @Test
    void assignInstructorToCourse_callGenerateEventAsync() {
        // GIVEN
        final var course = new Course().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockCourseRepository.findById(any())).thenReturn(of(course));
        final var newInstructor = buildInstructor();
        when(mockInstructorService.getOrCreateInstructorById(any())).thenReturn(Mono.just(newInstructor));

        final var courseInstructor = new CourseInstructor().setId(UUID.fromString("00000000-0000-0000-0000-000000000028"));
        when(mockCourseInstructorRepository.save(any())).thenReturn(courseInstructor);

        // WHEN
        final var mono = service.assignInstructorToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseAssignmentEventProducer).generateInstructorAssignmentEventAsync(courseInstructor);
    }

    @Test
    void assignInstructorToCourse_instructorIsAlreadyAssigned_neverCallSave() {
        // GIVEN
        when(mockCourseRepository.findById(any())).thenReturn(of(new Course()));
        when(mockInstructorService.getOrCreateInstructorById(any())).thenReturn(Mono.just(new Instructor()));
        when(mockCourseInstructorRepository.findByCourseIdAndInstructorId(any(), any())).thenReturn(new CourseInstructor());

        // WHEN
        final var mono = service.assignInstructorToCourse(null, null);

        // THEN
        StepVerifier.create(mono).expectNextCount(0).verifyComplete();
        verify(mockCourseInstructorRepository, never()).save(any());
    }

    private Instructor buildInstructor() {
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        return new Instructor().setId(instructorId);
    }

    private Course buildCourse() {
        return new Course().setInstructors(new HashSet<>());
    }

}
