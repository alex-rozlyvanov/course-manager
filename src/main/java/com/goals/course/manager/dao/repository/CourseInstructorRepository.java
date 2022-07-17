package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.CourseInstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseInstructorRepository extends CrudRepository<CourseInstructor, UUID> {
    @Query("""
            SELECT ci FROM CourseInstructor ci
            WHERE ci.course.id = :courseId AND ci.instructor.id = :instructorId""")
    CourseInstructor findByCourseIdAndInstructorId(@Param("courseId") final UUID courseId, @Param("instructorId") final UUID instructorId);
}
