package com.goals.course.manager.controller;

import com.goals.course.manager.dto.AssignInstructorRequest;
import com.goals.course.manager.dto.CourseDTO;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.enums.CourseFilters;
import com.goals.course.manager.mapper.CourseMapper;
import com.goals.course.manager.mapper.LessonMapper;
import com.goals.course.manager.service.*;
import com.goals.course.manager.service.filtering.course.CourseFilteringService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.UUID;

@RestController
@RequestMapping("/api/manager/courses")
@AllArgsConstructor
public class CourseController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final CourseFilteringService courseFilteringService;
    private final CourseCreationService courseCreationService;
    private final CourseMapper courseMapper;
    private final CourseAssigmentService courseAssigmentService;
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @GetMapping
    public Flux<CourseDTO> getAllCourse(@QueryParam("filter") final CourseFilters filter,
                                        final Authentication authentication) {
        final var courseFilter = courseFilteringService.getCourseFilter(filter, authentication);
        return courseService.getAllCourses(courseFilter);
    }

    @PostMapping
    public Mono<CourseDTO> createCourse(@RequestBody final CourseDTO courseDTO) {
        return courseCreationService.createCourse(courseDTO);
    }

    @PatchMapping("/{courseId}/instructors")
    public Mono<Void> assignCourseInstructor(@PathVariable("courseId") final UUID courseId,
                                             @RequestBody @Valid final AssignInstructorRequest instructorRequest) {
        return courseAssigmentService.assignInstructorToCourse(courseId, instructorRequest.getInstructorId());
    }

    @GetMapping("/{courseId}")
    @Transactional
    public Mono<CourseDTO> getCourseById(@PathVariable("courseId") final UUID courseId) {
        final var course = courseService.getCourseById(courseId);

        return courseMapper.mapToCourseDTO(course);
    }

    @PostMapping("/{courseId}/lessons")
    public Mono<LessonDTO> createCourseLesson(@PathVariable("courseId") final UUID courseId, @RequestBody final LessonDTO lessonDTO) {
        final var createdLesson = lessonService.createLesson(courseId, lessonDTO);

        return lessonMapper.mapToLessonDTO(createdLesson);
    }

    @GetMapping("/{courseId}/lessons")
    public Flux<LessonDTO> getCourseLessons(@PathVariable("courseId") final UUID courseId) {
        return lessonService.getLessonsByCourseId(courseId);
    }

    @GetMapping("/{courseId}/students")
    public Flux<UserDTO> getCourseStudents(@PathVariable("courseId") final UUID courseId) {
        return studentService.getStudentsByCourseId(courseId);
    }

}
