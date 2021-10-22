package com.goals.course.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LessonNotFoundException extends RuntimeException {
    public LessonNotFoundException(final UUID userId) {
        super("User with id '%s' not found".formatted(userId));
    }

    public LessonNotFoundException(final String message) {
        super(message);
    }
}
