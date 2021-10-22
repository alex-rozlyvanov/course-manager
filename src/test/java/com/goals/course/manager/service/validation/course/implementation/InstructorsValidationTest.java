package com.goals.course.manager.service.validation.course.implementation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.InstructorDTO;
import com.goals.course.manager.service.validation.implementation.ValidationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class InstructorsValidationTest {

    private final InstructorsValidation service = new InstructorsValidation();


    @ParameterizedTest
    @MethodSource("instructorSource")
    void validateCreation_instructor_checkResult(final Set<InstructorDTO> instructorDTOSet,
                                                 final ValidationResult expectedResult) {
        // GIVEN
        final var courseDTO = CourseDTO.builder()
                .instructors(instructorDTOSet)
                .build();

        // WHEN
        final var result = service.validate(courseDTO);

        // THEN
        assertThat(result.isValid()).isEqualTo(expectedResult.isValid());
        assertThat(result.getMessage()).isEqualTo(expectedResult.getMessage());
    }

    private static Stream<Arguments> instructorSource() {
        return Stream.of(
                of(buildInstructorDTOSetWithId(), ValidationResult.valid()),
                of(Set.of(), ValidationResult.invalid("Course should have at least one instructor")),
                of(null, ValidationResult.invalid("Course should have at least one instructor"))
        );
    }

    private static Set<InstructorDTO> buildInstructorDTOSetWithId() {
        final var instructorId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return Set.of(InstructorDTO.builder().id(instructorId).build());
    }


}
