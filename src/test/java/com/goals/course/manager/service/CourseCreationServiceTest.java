package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Course;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.exception.CourseCannotBeCreatedException;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.service.validation.CourseCreationValidation;
import com.goals.course.manager.service.validation.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCreationServiceTest {

    @Mock
    private CourseMapper mockCourseMapper;
    @Mock
    private CourseRepository mockCourseRepository;
    @Mock
    private CourseCreationValidation mockCourseCreationValidation;
    @Mock
    private CourseAssigmentService mockCourseAssigmentService;
    @Mock
    private LessonService mockLessonService;
    @InjectMocks
    private CourseCreationService service;

    @Test
    void createCourse_callValidateCreation() {
        // GIVEN
        final var courseDTO = buildCourse();
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());
        when(mockCourseRepository.save(any())).thenReturn(new Course());

        // WHEN
        service.createCourse(courseDTO);

        // THEN
        verify(mockCourseCreationValidation).validateCreation(courseDTO);
    }

    @Test
    void createCourse_validationFailed_throwException() {
        // GIVEN
        final var courseDTO = buildCourse();
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.invalid("test message"));

        // WHEN
        final var expectedException = assertThrows(
                CourseCannotBeCreatedException.class,
                () -> service.createCourse(courseDTO)
        );

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("test message");
        verify(mockCourseCreationValidation).validateCreation(courseDTO);
    }

    @Test
    void createCourse_validationFailed_neverCallMapToCourse() {
        // GIVEN
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.invalid("test message"));

        // WHEN
        assertThrows(CourseCannotBeCreatedException.class, () -> service.createCourse(null));

        // THEN
        verify(mockCourseMapper, never()).mapToCourse(any());
    }

    @Test
    void createCourse_callMapToCourse() {
        // GIVEN
        final var courseDTO = buildCourse();
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());
        when(mockCourseRepository.save(any())).thenReturn(new Course());

        // WHEN
        service.createCourse(courseDTO);

        // THEN
        verify(mockCourseMapper).mapToCourse(courseDTO);
    }

    @Test
    void createCourse_callSave() {
        // GIVEN
        final var mapperCourse = new Course().setTitle("mapped title");
        when(mockCourseMapper.mapToCourse(any())).thenReturn(mapperCourse);
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());
        when(mockCourseRepository.save(any())).thenReturn(new Course());

        // WHEN
        service.createCourse(buildCourse());

        // THEN
        verify(mockCourseRepository).save(mapperCourse);
    }

    @Test
    void createCourse_callAssignInstructorToCourse() {
        // GIVEN
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());

        final var courseInstructors = Set.of(
                buildCourseInstructor(UUID.fromString("00000000-0000-0000-0000-000000000002")),
                buildCourseInstructor(UUID.fromString("00000000-0000-0000-0000-000000000003"))
        );
        final var savedCourse = new Course()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setInstructors(courseInstructors);
        when(mockCourseRepository.save(any())).thenReturn(savedCourse);

        // WHEN
        service.createCourse(buildCourse());

        // THEN
        verify(mockCourseAssigmentService)
                .assignInstructorToCourse(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000002"));
        verify(mockCourseAssigmentService)
                .assignInstructorToCourse(UUID.fromString("00000000-0000-0000-0000-000000000001"), UUID.fromString("00000000-0000-0000-0000-000000000003"));
    }

    @Test
    void createCourse_callCreateLesson() {
        // GIVEN
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());

        final var lessonDTO1 = buildLesson(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var lessonDTO2 = buildLesson(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var lessons = Set.of(
                lessonDTO1,
                lessonDTO2
        );

        final var savedCourse = new Course()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockCourseRepository.save(any())).thenReturn(savedCourse);

        // WHEN
        service.createCourse(CourseDTO.builder().lessons(lessons).build());

        // THEN
        verify(mockLessonService)
                .createLesson(UUID.fromString("00000000-0000-0000-0000-000000000001"), lessonDTO1);
        verify(mockLessonService)
                .createLesson(UUID.fromString("00000000-0000-0000-0000-000000000001"), lessonDTO2);
    }

    @Test
    void createCourse_callMapToCourseDTO() {
        // GIVEN
        final var savedCourse = new Course()
                .setId(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .setTitle("mapped title");
        when(mockCourseRepository.save(any())).thenReturn(savedCourse);
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());

        // WHEN
        service.createCourse(buildCourse());

        // THEN
        verify(mockCourseMapper).mapToCourseDTO(savedCourse);
    }

    @Test
    void createCourse_checkResult() {
        // GIVEN
        final var mappedSavedCourse = buildCourse();
        when(mockCourseMapper.mapToCourseDTO(any())).thenReturn(Mono.just(mappedSavedCourse));
        when(mockCourseCreationValidation.validateCreation(any())).thenReturn(ValidationResult.valid());
        when(mockCourseRepository.save(any())).thenReturn(new Course());

        // WHEN
        final var mono = service.createCourse(buildCourse());

        // THEN
        StepVerifier.create(mono)
                .expectNext(mappedSavedCourse)
                .verifyComplete();
    }

    private CourseDTO buildCourse() {
        return CourseDTO.builder()
                .title("New course!")
                .lessons(Set.of())
                .build();
    }

    private LessonDTO buildLesson(final UUID lessonId) {
        return LessonDTO.builder().id(lessonId).build();
    }

    private CourseInstructor buildCourseInstructor(final UUID instructorId) {
        return new CourseInstructor().setInstructor(new Instructor().setId(instructorId));
    }

}
