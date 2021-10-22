package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.*;
import com.goals.course.manager.dao.repository.CourseInstructorRepository;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dao.repository.CourseStudentRepository;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.service.CourseAssigmentService;
import com.goals.course.manager.service.InstructorService;
import com.goals.course.manager.service.StudentService;
import com.goals.course.manager.service.validation.CourseAssignmentValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CourseAssigmentServiceImpl implements CourseAssigmentService {
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseInstructorRepository courseInstructorRepository;
    private final CourseAssignmentValidation courseAssignmentValidation;

    @Override
    @Transactional
    public void assignStudentToCourse(final UUID courseId, final UUID studentId) {
        log.info("Assigning student '{}' to course  {}", studentId, courseId);
        final var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        final var student = studentService.getOrCreateStudent(studentId);

        courseStudentRepository.findByCourse_IdAndStudent_Id(course.getId(), student.getId())
                .ifPresentOrElse(
                        cs -> log.info("Student '{}' is already assigned to the course '{}'", student.getId(), course.getId()),
                        () -> createNewStudentAssignment(course, student)
                );
    }

    private void createNewStudentAssignment(final Course course, final Student student) {
        final var validationResult = courseAssignmentValidation.canStudentTakeACourse(student);

        if (validationResult.isNotValid()) {
            throw new IllegalStateException(validationResult.getMessage());
        }

        final var courseStudent = new CourseStudent()
                .setCourse(course)
                .setStudent(student)
                .setCourseIsCompleted(false);

        courseStudentRepository.save(courseStudent);
        log.info("Student '{}' assigned to the course '{}'", student.getId(), course.getId());
    }

    @Override
    @Transactional
    public void assignInstructorToCourse(final UUID courseId, final UUID instructorId) {
        log.info("Assigning instructor '{}' to course  {}", instructorId, courseId);
        final var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        final var instructor = instructorService.getOrCreateInstructorById(instructorId);

        courseInstructorRepository.findByCourse_IdAndInstructor_Id(courseId, instructorId)
                .ifPresentOrElse(
                        ci -> log.info("Instructor '{}' is already assigned to the course '{}'", instructorId, courseId),
                        () -> createNewInstructorAssignment(instructor, course)
                );
    }

    private void createNewInstructorAssignment(final Instructor instructor, final Course course) {
        final var courseStudent = new CourseInstructor()
                .setCourse(course)
                .setInstructor(instructor);

        courseInstructorRepository.save(courseStudent);
        log.info("Instructor '{}' assigned to the course '{}'", instructor.getId(), course.getId());
    }
}
