package com.goals.course.manager.controller;

import com.goals.course.manager.dto.UserDTO;

import java.util.UUID;

public interface StudentController {
    UserDTO getStudentById(final UUID studentId);
}
