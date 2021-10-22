package com.goals.course.manager.controller.implementation;

import com.goals.course.manager.controller.LessonController;
import com.goals.course.manager.dto.LessonDTO;
import com.goals.course.manager.service.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/manager/lessons")
@AllArgsConstructor
public class LessonControllerImpl implements LessonController {

    private final LessonService lessonService;

    @Override
    @GetMapping("/{lessonId}")
    public LessonDTO getLessonById(@PathVariable("lessonId") final UUID lessonId) {
        return lessonService.getLessonById(lessonId);
    }

}
