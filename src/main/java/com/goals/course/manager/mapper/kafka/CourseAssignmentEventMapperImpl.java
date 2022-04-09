package com.goals.course.manager.mapper.kafka;

import com.goals.course.avro.CourseAssignmentEventAvro;
import com.goals.course.manager.dao.entity.CourseInstructor;
import com.goals.course.manager.dao.entity.CourseStudent;
import com.goals.course.manager.dao.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CourseAssignmentEventMapperImpl implements CourseAssignmentEventMapper {

    @Override
    public CourseAssignmentEventAvro mapFromCourseStudent(final CourseStudent courseStudent) {

        return CourseAssignmentEventAvro.newBuilder()
                .setId(courseStudent.getId().toString())
                .setUserId(courseStudent.getStudent().getId().toString())
                .setCourseId(courseStudent.getCourse().getId().toString())
                .setRole(Roles.STUDENT.toString())
                .build();
//        return CourseAssignmentEvent.builder()
//                .id(courseStudent.getId())
//                .userId(courseStudent.getStudent().getId())
//                .courseId(courseStudent.getCourse().getId())
//                .role(Roles.STUDENT)
//                .build();
//        return null; // TODO impl
    }

    @Override
    public CourseAssignmentEventAvro mapFromCourseInstructor(final CourseInstructor courseInstructor) {
        return CourseAssignmentEventAvro.newBuilder()
                .setId(courseInstructor.getId().toString())
                .setUserId(courseInstructor.getInstructor().getId().toString())
                .setCourseId(courseInstructor.getCourse().getId().toString())
                .setRole(Roles.INSTRUCTOR.toString())
                .build();
//        return CourseAssignmentEvent.builder()
//                .id(courseInstructor.getId())
//                .userId(courseInstructor.getInstructor().getId())
//                .courseId(courseInstructor.getCourse().getId())
//                .role(Roles.INSTRUCTOR)
//                .build();
//        return null; // TODO impl
    }
}
