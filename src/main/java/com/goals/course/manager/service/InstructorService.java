package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Instructor;

import java.util.UUID;

public interface InstructorService {
    Instructor getOrCreateInstructorById(final UUID instructorId);
}
