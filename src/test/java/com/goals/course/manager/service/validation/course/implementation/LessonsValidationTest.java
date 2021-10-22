package com.goals.course.manager.service.validation.course.implementation;

import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.service.validation.implementation.ValidationResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class LessonsValidationTest {
    private final LessonsValidation service = new LessonsValidation();

    @BeforeEach
    public void before() {
        ReflectionTestUtils.setField(service, "numberOfRequiredLessons", 5);
    }

    @ParameterizedTest
    @MethodSource("lessonsSource")
    void validateCreation_lessons_checkResult(final Set<LessonDTO> lessons,
                                              final ValidationResult expectedResult) {
        // GIVEN
        final var courseDTO = CourseDTO.builder()
                .lessons(lessons)
                .build();

        // WHEN
        final var result = service.validate(courseDTO);

        // THEN
        assertThat(result.isValid()).isEqualTo(expectedResult.isValid());
        assertThat(result.getMessage()).isEqualTo(expectedResult.getMessage());
    }


    private static Stream<Arguments> lessonsSource() {
        return Stream.of(
                of(buildLessonsDTOSet(), ValidationResult.valid()),
                of(Set.of(), ValidationResult.invalid("Course should have at least 5 lessons")),
                of(null, ValidationResult.invalid("Course should have at least 5 lessons"))
        );
    }

    private static Set<LessonDTO> buildLessonsDTOSet() {
        return Set.of(
                buildLesson("00000000-0000-0000-0000-000000000001"),
                buildLesson("00000000-0000-0000-0000-000000000002"),
                buildLesson("00000000-0000-0000-0000-000000000003"),
                buildLesson("00000000-0000-0000-0000-000000000004"),
                buildLesson("00000000-0000-0000-0000-000000000005")
        );
    }

    private static LessonDTO buildLesson(final String lessonId) {
        return LessonDTO.builder().id(UUID.fromString(lessonId)).build();
    }
}
