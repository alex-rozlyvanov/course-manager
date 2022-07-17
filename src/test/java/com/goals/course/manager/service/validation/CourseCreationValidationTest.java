package com.goals.course.manager.service.validation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.service.validation.course.CourseValidation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CourseCreationValidationTest {

    @Test
    void validateCreation_passed_callValidate() {
        // GIVEN
        final var passedValidation1 = buildPassedCourseValidationMock();
        final var passedValidation2 = buildPassedCourseValidationMock();
        final var passedValidation3 = buildPassedCourseValidationMock();
        final List<CourseValidation> validations = List.of(
                passedValidation1,
                passedValidation2,
                passedValidation3
        );

        final var courseDTO = CourseDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .build();

        // WHEN
        new CourseCreationValidation(validations).validateCreation(courseDTO);

        // THEN
        verify(passedValidation1).validate(courseDTO);
        verify(passedValidation2).validate(courseDTO);
        verify(passedValidation3).validate(courseDTO);
    }

    @Test
    void validateCreation_passed_checkResult() {
        // GIVEN
        final List<CourseValidation> validations = List.of(
                buildPassedCourseValidationMock(),
                buildPassedCourseValidationMock(),
                buildPassedCourseValidationMock(),
                buildPassedCourseValidationMock(),
                buildPassedCourseValidationMock()
        );

        final var courseDTO = CourseDTO.builder().build();

        // WHEN
        final var result = new CourseCreationValidation(validations).validateCreation(courseDTO);

        // THEN
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validateCreation_failed_callValidate() {
        // GIVEN
        // GIVEN
        final var passedValidation0 = buildPassedCourseValidationMock();
        final var passedValidation1 = buildPassedCourseValidationMock();
        final var passedValidation2 = buildCourseValidationMock(ValidationResult.invalid("failed"));
        final var passedValidation3 = mock(CourseValidation.class);
        final var passedValidation4 = mock(CourseValidation.class);
        final List<CourseValidation> validations = new ArrayList<>();
        validations.add(passedValidation0);
        validations.add(passedValidation1);
        validations.add(passedValidation2);
        validations.add(passedValidation3);
        validations.add(passedValidation3);

        final var courseDTO = CourseDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .build();

        // WHEN
        new CourseCreationValidation(validations).validateCreation(courseDTO);

        // THEN
        verify(passedValidation1).validate(courseDTO);
        verify(passedValidation2).validate(courseDTO);
        verify(passedValidation3, never()).validate(any());
        verify(passedValidation4, never()).validate(any());
    }

    @Test
    void validateCreation_failed_checkResult() {
        // GIVEN
        final var failed = ValidationResult.invalid("Validation failed");
        final List<CourseValidation> validations = List.of(
                buildPassedCourseValidationMock(),
                buildCourseValidationMock(failed)
        );

        final var courseDTO = CourseDTO.builder().build();

        // WHEN
        final var result = new CourseCreationValidation(validations).validateCreation(courseDTO);

        // THEN
        assertThat(result).isSameAs(failed);
    }

    private CourseValidation buildPassedCourseValidationMock() {
        final CourseValidation mockCourseValidation = mock(CourseValidation.class);
        when(mockCourseValidation.validate(any())).thenReturn(ValidationResult.valid());
        return mockCourseValidation;
    }

    private CourseValidation buildCourseValidationMock(final ValidationResult failedValidationResult) {
        final CourseValidation mockCourseValidation = mock(CourseValidation.class);
        when(mockCourseValidation.validate(any())).thenReturn(failedValidationResult);
        return mockCourseValidation;
    }

}
