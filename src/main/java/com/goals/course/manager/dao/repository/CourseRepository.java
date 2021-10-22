package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("""
                SELECT c FROM Course c \s
                LEFT JOIN CourseInstructor ci \s
                ON c.id = ci.course.id WHERE ci.instructor.id = :instructorId
            """)
    List<Course> findByInstructorId(@Param("instructorId") final UUID instructorId);

    @Query("""
                SELECT c FROM Course c \s
                LEFT JOIN CourseStudent cs \s
                ON c.id = cs.course.id WHERE cs.student.id = :studentId
            """)
    List<Course> findByStudentId(@Param("studentId") final UUID studentId);
}
