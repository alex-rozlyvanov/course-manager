package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.CourseStudent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CourseStudentRepository extends CrudRepository<CourseStudent, UUID> {
    @Query("""
            SELECT cs FROM CourseStudent cs
            WHERE cs.course.id = :courseId AND cs.student.id = :studentId""")
    CourseStudent findByCourseIdAndStudentId(@Param("courseId") final UUID courseId, @Param("studentId") final UUID studentId);
}
