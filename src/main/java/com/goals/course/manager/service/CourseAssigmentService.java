package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.*;
import com.goals.course.manager.dao.repository.CourseInstructorRepository;
import com.goals.course.manager.dao.repository.CourseRepository;
import com.goals.course.manager.dao.repository.CourseStudentRepository;
import com.goals.course.manager.exception.CourseNotFoundException;
import com.goals.course.manager.service.kafka.CourseAssignmentEventProducer;
import com.goals.course.manager.service.validation.CourseAssignmentValidation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class CourseAssigmentService {
    private final StudentService studentService;
    private final InstructorService instructorService;
    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final CourseInstructorRepository courseInstructorRepository;
    private final CourseAssignmentValidation courseAssignmentValidation;
    private final CourseAssignmentEventProducer courseAssignmentEventProducer;

    @Transactional
    public Mono<Void> assignStudentToCourse(final UUID courseId, final UUID studentId) {
        log.info("Assigning student '{}' to course  {}", studentId, courseId);
        final var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return studentService.getOrCreateStudent(studentId)
                .flatMap(student -> createAssignmentIfNeeded(course, student))
                .then();
    }

    private Mono<CourseStudent> createAssignmentIfNeeded(final Course course, final Student student) {
        return Mono.justOrEmpty(courseStudentRepository.findByCourseIdAndStudentId(course.getId(), student.getId()))
                .map(cs -> {
                    log.info("Student '{}' is already assigned to the course '{}'", student.getId(), course.getId());
                    return cs;
                })
                .switchIfEmpty(Mono.fromSupplier(() -> createNewStudentAssignment(course, student)));
    }

    private CourseStudent createNewStudentAssignment(final Course course, final Student student) {
        final var validationResult = courseAssignmentValidation.canStudentTakeACourse(student);

        if (validationResult.isNotValid()) {
            throw new IllegalStateException(validationResult.message());
        }

        final var courseStudent = new CourseStudent()
                .setCourse(course)
                .setStudent(student)
                .setCourseIsCompleted(false);

        final var saved = courseStudentRepository.save(courseStudent);
        log.info("Student '{}' assigned to the course '{}'", student.getId(), course.getId());
        courseAssignmentEventProducer.generateStudentAssignmentEventAsync(saved);

        return saved;
    }

    @Transactional
    public Mono<Void> assignInstructorToCourse(final UUID courseId, final UUID instructorId) {
        log.info("Assigning instructor '{}' to course  {}", instructorId, courseId);
        final var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return instructorService.getOrCreateInstructorById(instructorId)
                .flatMap(instructor -> createAssignmentIfNeeded(course, instructor))
                .then();
    }

    private Mono<CourseInstructor> createAssignmentIfNeeded(final Course course, final Instructor instructor) {
        return Mono.justOrEmpty(courseInstructorRepository.findByCourseIdAndInstructorId(course.getId(), instructor.getId()))
                .map(ci -> {
                    log.info("Instructor '{}' is already assigned to the course '{}'", instructor.getId(), course.getId());
                    return ci;
                })
                .switchIfEmpty(Mono.fromSupplier(() -> createNewInstructorAssignment(course, instructor)));
    }

    private CourseInstructor createNewInstructorAssignment(final Course course, final Instructor instructor) {
        final var courseStudent = new CourseInstructor()
                .setCourse(course)
                .setInstructor(instructor);

        final var saved = courseInstructorRepository.save(courseStudent);
        log.info("Instructor '{}' assigned to the course '{}'", instructor.getId(), course.getId());
        courseAssignmentEventProducer.generateInstructorAssignmentEventAsync(saved);
        return saved;
    }
}
