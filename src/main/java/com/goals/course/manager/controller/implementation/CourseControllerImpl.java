package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.controller.CourseController;
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

import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/manager/courses")
@AllArgsConstructor
public class CourseControllerImpl implements CourseController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final CourseFilteringService courseFilteringService;
    private final CourseCreationService courseCreationService;
    private final CourseMapper courseMapper;
    private final CourseAssigmentService courseAssigmentService;
    private final LessonService lessonService;
    private final LessonMapper lessonMapper;

    @Override
    @GetMapping
    public List<CourseDTO> getAllCourse(@QueryParam("filter") final CourseFilters filter,
                                        final Authentication authentication) {
        final var courseFilter = courseFilteringService.getCourseFilter(filter, authentication);
        return courseService.getAllCourses(courseFilter);
    }

    @Override
    @PostMapping
    public CourseDTO createCourse(@RequestBody final CourseDTO courseDTO) {
        return courseCreationService.createCourse(courseDTO);
    }

    @Override
    @PatchMapping("/{courseId}/instructors")
    public void assignCourseInstructor(@PathVariable("courseId") final UUID courseId,
                                       @RequestBody @Valid final AssignInstructorRequest instructorRequest) {
        courseAssigmentService.assignInstructorToCourse(courseId, instructorRequest.getInstructorId());
    }

    @Override
    @GetMapping("/{courseId}")
    @Transactional
    public CourseDTO getCourseById(@PathVariable("courseId") final UUID courseId) {
        final var course = courseService.getCourseById(courseId);

        return courseMapper.mapToCourseDTO(course);
    }

    @Override
    @PostMapping("/{courseId}/lessons")
    public LessonDTO createCourseLesson(@PathVariable("courseId") final UUID courseId, @RequestBody final LessonDTO lessonDTO) {
        final var createdLesson = lessonService.createLesson(courseId, lessonDTO);

        return lessonMapper.mapToLessonDTO(createdLesson);
    }

    @Override
    @GetMapping("/{courseId}/lessons")
    public List<LessonDTO> getCourseLessons(@PathVariable("courseId") final UUID courseId) {
        return lessonService.getLessonsByCourseId(courseId);
    }

    @Override
    @GetMapping("/{courseId}/students")
    public List<UserDTO> getCourseStudents(@PathVariable("courseId") final UUID courseId) {
        return studentService.getStudentsByCourseId(courseId);
    }

}
