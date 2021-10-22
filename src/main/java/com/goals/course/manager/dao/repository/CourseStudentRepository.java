package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.CourseStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseStudentRepository extends CrudRepository<CourseStudent, UUID> {
    Optional<CourseStudent> findByCourse_IdAndStudent_Id(final UUID courseId, final UUID studentId);
}
