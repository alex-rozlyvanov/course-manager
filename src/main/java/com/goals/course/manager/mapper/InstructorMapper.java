package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dto.InstructorDTO;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstructorMapper {
    public Set<CourseInstructor> mapToCourseInstructorSet(final Set<InstructorDTO> instructorsDTOSet) {
        return instructorsDTOSet
                .stream()
                .map(this::buildCourseInstructor)
                .collect(Collectors.toSet());
    }

    private CourseInstructor buildCourseInstructor(final InstructorDTO instructorDTO) {
        final var instructor = new Instructor().setId(instructorDTO.getId());
        return new CourseInstructor().setInstructor(instructor);
    }

    public Set<InstructorDTO> mapToInstructorDTOSet(final Set<CourseInstructor> courseInstructorSet) {
        return courseInstructorSet
                .stream()
                .map(this::buildInstructorDTO)
                .collect(Collectors.toSet());
    }

    private InstructorDTO buildInstructorDTO(final CourseInstructor courseInstructor) {
        return InstructorDTO.builder()
                .id(courseInstructor.getInstructor().getId())
                .build();
    }
}
