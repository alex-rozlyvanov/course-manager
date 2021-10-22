package com.goals.course.manager.service.validation;

import com.goals.course.manager.dao.entity.CourseStudent;
import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.service.validation.implementation.CourseAssignmentValidationImpl;
import com.goals.course.manager.service.validation.implementation.ValidationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class CourseAssignmentValidationImplTest {

    private final CourseAssignmentValidationImpl service = new CourseAssignmentValidationImpl();

    @ParameterizedTest
    @MethodSource("studentsSource")
    void canStudentTakeACourse_checkResult(final Student student, final ValidationResult expected) {
        // GIVEN
        ReflectionTestUtils.setField(service, "maxNumberOfCoursesStudentCanTake", 3);

        // WHEN
        final var result = service.canStudentTakeACourse(student);

        // THEN
        assertThat(result.isValid()).isEqualTo(expected.isValid());
        assertThat(result.getMessage()).isEqualTo(expected.getMessage());
    }

    private static Stream<Arguments> studentsSource() {
        return Stream.of(
                of(new Student().setCourses(getNCourses(2, 0)), ValidationResult.valid()),
                of(new Student().setCourses(getNCourses(2, 1)), ValidationResult.valid()),
                of(new Student().setCourses(getNCourses(3, 0)), ValidationResult.invalid("Student cannot take more courses. Maximum is 3")),
                of(new Student().setCourses(getNCourses(3, 1)), ValidationResult.invalid("Student cannot take more courses. Maximum is 3")),
                of(new Student().setCourses(getNCourses(4, 0)), ValidationResult.invalid("Student cannot take more courses. Maximum is 3")),
                of(new Student().setCourses(getNCourses(4, 1)), ValidationResult.invalid("Student cannot take more courses. Maximum is 3"))
        );
    }

    private static Set<CourseStudent> getNCourses(final int numberOfCourses, final int numberOfCompletedCourses) {

        final var completedCourses = IntStream.rangeClosed(1, numberOfCompletedCourses)
                .mapToObj(i -> buildCourseStudent(true))
                .collect(Collectors.toSet());

        final var notCompletedCourses = IntStream.rangeClosed(1, numberOfCourses)
                .mapToObj(i -> buildCourseStudent(false))
                .collect(Collectors.toSet());

        completedCourses.addAll(notCompletedCourses);

        return completedCourses;
    }

    private static CourseStudent buildCourseStudent(boolean complete) {
        return new CourseStudent()
                .setId(UUID.randomUUID())
                .setCourseIsCompleted(complete);
    }
}
