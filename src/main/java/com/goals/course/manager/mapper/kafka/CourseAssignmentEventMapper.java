package com.goals.course.manager.mapper.kafka;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.CourseStudent;

public interface CourseAssignmentEventMapper {
    CourseAssignmentEventAvro mapFromCourseStudent(final CourseStudent courseStudent);

    CourseAssignmentEventAvro mapFromCourseInstructor(final CourseInstructor courseInstructor);
}
