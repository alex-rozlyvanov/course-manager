package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.CourseInstructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseInstructorRepository extends CrudRepository<CourseInstructor, UUID> {
    Optional<CourseInstructor> findByCourse_IdAndInstructor_Id(final UUID courseId, final UUID instructorId);
}
