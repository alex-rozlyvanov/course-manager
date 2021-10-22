package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @Query("""
            SELECT l FROM Lesson l \s
            LEFT OUTER JOIN Course c ON l.course.id = c.id \s
            WHERE c.id = :courseId
            """)
    List<Lesson> findAllByCourseId(@Param("courseId") final UUID courseId);
}
