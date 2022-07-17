package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dto.InstructorDTO;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InstructorsMapperTest {

    private final InstructorMapper service = new InstructorMapper();

    @Test
    void mapToInstructors_id_checkResult() {
        // GIVEN
        final var instructorDTO1 = InstructorDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var instructorDTO2 = InstructorDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000002")).build();
        final var instructorDTOSet = Set.of(instructorDTO1, instructorDTO2);

        // WHEN
        final var result = service.mapToCourseInstructorSet(instructorDTOSet);

        // THEN
        assertThat(result)
                .isNotEmpty()
                .extracting(CourseInstructor::getInstructor)
                .extracting(Instructor::getId)
                .extracting(UUID::toString)
                .containsExactlyInAnyOrder("00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002");
    }

    @Test
    void mapToInstructorDTOSet_id_checkResult() {
        // GIVEN
        final var courseInstructor1 = new CourseInstructor().setInstructor(new Instructor().setId(UUID.fromString("00000000-0000-0000-0000-000000000001")));
        final var courseInstructor2 = new CourseInstructor().setInstructor(new Instructor().setId(UUID.fromString("00000000-0000-0000-0000-000000000002")));
        final var courseInstructorSet = Set.of(courseInstructor1, courseInstructor2);

        // WHEN
        final var result = service.mapToInstructorDTOSet(courseInstructorSet);

        // THEN
        assertThat(result)
                .isNotEmpty()
                .extracting(InstructorDTO::getId)
                .extracting(UUID::toString)
                .containsExactlyInAnyOrder("00000000-0000-0000-0000-000000000001", "00000000-0000-0000-0000-000000000002");
    }

}
