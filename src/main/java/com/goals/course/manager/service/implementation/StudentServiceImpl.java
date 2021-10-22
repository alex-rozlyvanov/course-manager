package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Student;
import com.goals.course.manager.dao.repository.StudentRepository;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.StudentNotFoundException;
import com.goals.course.manager.exception.UserNotFoundException;
import com.goals.course.manager.mapper.StudentMapper;
import com.goals.course.manager.service.CourseService;
import com.goals.course.manager.service.StudentService;
import com.goals.course.manager.service.UserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final CourseService courseService;
    private final UserProvider userProvider;

    @Override
    @Transactional
    public Student getOrCreateStudent(final UUID userId) {
        return userProvider.findUserById(userId)
                .filter(this::isStudent)
                .map(this::resolveStudent)
                .orElseThrow(() -> new UserNotFoundException("Student with id '%s' not found".formatted(userId)));
    }

    @Override
    public UserDTO getStudentById(final UUID studentId) {
        final var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student '%s' not found!".formatted(studentId)));

        return userProvider.findUserById(student.getId())
                .orElseThrow(() -> new UserNotFoundException("User '%s' not found!".formatted(student.getId())));
    }

    @Override
    public List<UserDTO> getStudentsByCourseId(final UUID courseId) {
        final var course = courseService.getCourseById(courseId);
        final var students = studentRepository.findAllByCourseId(course.getId());
        return students.stream().map(studentMapper::toUserDTO).toList();
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
