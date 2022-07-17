package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dao.repository.StudentRepository;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.StudentNotFoundException;
import com.goals.course.manager.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final CourseService courseService;
    private final UserProvider userProvider;

    @Transactional
    public Mono<Student> getOrCreateStudent(final UUID userId) {
        return userProvider.findUserById(userId)
                .filter(this::isStudent)
                .map(this::resolveStudent)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("Student with id '%s' not found".formatted(userId))));
    }

    public Mono<UserDTO> getStudentById(final UUID studentId) {
        final var studentMono = Mono.justOrEmpty(studentRepository.findById(studentId))
                .switchIfEmpty(Mono.error(() -> new StudentNotFoundException("Student '%s' not found!".formatted(studentId))));

        return studentMono
                .flatMap(student -> userProvider.findUserById(student.getId()))
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("User '%s' not found!".formatted(studentId))));
    }

    public Flux<UserDTO> getStudentsByCourseId(final UUID courseId) {
        final var course = courseService.getCourseById(courseId);
        final var students = studentRepository.findAllByCourseId(course.getId());

        return Flux.fromIterable(students).flatMap(s -> userProvider.findUserById(s.getId()));
    }

    private boolean isStudent(final UserDTO userDTO) {
        if (userDTO.isStudent()) {
            return true;
        }
        throw new IllegalStateException("User is not a student!");
    }

    private Student resolveStudent(final UserDTO userDTO) {
        return studentRepository.findById(userDTO.getId())
                .orElseGet(() -> {
                    final var student = new Student().setId(userDTO.getId());
                    return studentRepository.save(student);
                });
    }

}
