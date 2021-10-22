package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    Student getOrCreateStudent(final UUID userId);

    UserDTO getStudentById(final UUID studentId);

    List<UserDTO> getStudentsByCourseId(final UUID courseId);
}
