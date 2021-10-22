package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.controller.CourseAssigmentController;
import com.goals.course.manager.dto.AssignStudentToCourseRequest;
import com.goals.course.manager.service.CourseAssigmentService;
import com.goals.course.manager.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/courses")
@AllArgsConstructor
public class CourseAssigmentControllerImpl implements CourseAssigmentController {
    private final CourseAssigmentService courseAssigmentService;
    private final SecurityService securityService;

    @Override
    @PatchMapping("/{courseId}/take")
    public void assignUserToCourse(@PathVariable("courseId") final UUID courseId,
                                   @RequestBody final AssignStudentToCourseRequest assignStudentToCourseRequest) {
        courseAssigmentService.assignStudentToCourse(courseId, assignStudentToCourseRequest.userId());
    }

    @Override
    @PostMapping("/{courseId}/take")
    public void takeCourse(@PathVariable("courseId") final UUID courseId) {
        courseAssigmentService.assignStudentToCourse(courseId, securityService.getCurrentUser().getId());
    }
}
