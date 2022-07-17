package com.goals.course.manager.controller;

import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/students")
@AllArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{studentId}")
    public Mono<UserDTO> getStudentById(@PathVariable("studentId") final UUID studentId) {
        return studentService.getStudentById(studentId);
    }

}
