package com.goals.course.manager.dao.repository;

import com.goals.course.manager.dao.entity.Instructor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstructorRepository extends CrudRepository<Instructor, UUID> {
}
