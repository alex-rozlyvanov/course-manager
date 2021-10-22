package com.goals.course.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserIsNotInstructorException extends RuntimeException {
    public UserIsNotInstructorException(final String message) {
        super(message);
    }
}
