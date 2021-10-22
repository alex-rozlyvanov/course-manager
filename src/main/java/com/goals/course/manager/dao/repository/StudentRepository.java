package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.Student;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentRepository extends CrudRepository<Student, UUID> {
    @Query("""
            SELECT s FROM Student s \s
            LEFT OUTER JOIN CourseStudent cs ON s.id = cs.student.id \s
            WHERE cs.course.id = :courseId
            """)
    List<Student> findAllByCourseId(@Param("courseId") final UUID courseId);
}
