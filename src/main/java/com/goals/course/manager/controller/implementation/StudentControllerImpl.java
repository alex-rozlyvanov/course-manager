package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.controller.StudentController;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/students")
@AllArgsConstructor
public class StudentControllerImpl implements StudentController {

    private final StudentService studentService;

    @Override
    @GetMapping("/{studentId}")
    public UserDTO getStudentById(@PathVariable("studentId") final UUID studentId) {
        return studentService.getStudentById(studentId);
    }

}
