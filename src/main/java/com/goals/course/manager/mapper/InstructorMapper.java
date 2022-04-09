package com.goals.course.manager.mapper;

import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dto.InstructorDTO;

import java.util.Set;

public interface InstructorMapper {
    Set<CourseInstructor> mapToCourseInstructorSet(final Set<InstructorDTO> instructorsDTOSet);

    Set<InstructorDTO> mapToInstructorDTOSet(final Set<CourseInstructor> instructors);
}
