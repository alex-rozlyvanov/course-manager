package com.goals.course.manager.mapper.implementation;

import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dto.InstructorDTO;
import com.goals.course.manager.mapper.InstructorsMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InstructorsMapperImpl implements InstructorsMapper {
    @Override
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

    @Override
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
