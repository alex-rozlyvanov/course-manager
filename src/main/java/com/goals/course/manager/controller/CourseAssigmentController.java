package com.goals.course.manager.controller;

import com.goals.course.manager.dto.AssignStudentToCourseRequest;
import com.goals.course.manager.service.CourseAssigmentService;
import com.goals.course.manager.service.SecurityService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/courses")
@AllArgsConstructor
public class CourseAssigmentController {
    private final CourseAssigmentService courseAssigmentService;
    private final SecurityService securityService;

    @PatchMapping("/{courseId}/take")
    public Mono<Void> assignUserToCourse(@PathVariable("courseId") final UUID courseId,
                                         @RequestBody final AssignStudentToCourseRequest assignStudentToCourseRequest) {
        return courseAssigmentService.assignStudentToCourse(courseId, assignStudentToCourseRequest.userId());
    }

    @PostMapping("/{courseId}/take")
    public Mono<Void> takeCourse(@PathVariable("courseId") final UUID courseId) {
        return securityService.getCurrentUser()
                .flatMap(currentUser -> courseAssigmentService.assignStudentToCourse(courseId, currentUser.getId()));
    }
}
