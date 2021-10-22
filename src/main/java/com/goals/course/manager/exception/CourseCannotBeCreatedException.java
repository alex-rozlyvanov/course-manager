package com.goals.course.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CourseCannotBeCreatedException extends RuntimeException {
    public CourseCannotBeCreatedException(final String message) {
        super(message);
    }
}
