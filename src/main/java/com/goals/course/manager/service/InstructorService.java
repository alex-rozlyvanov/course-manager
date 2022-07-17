package com.goals.course.manager.service;

import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.repository.InstructorRepository;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.UserIsNotInstructorException;
import com.goals.course.manager.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InstructorService {
    private final UserProvider userProvider;
    private final InstructorRepository instructorRepository;

    public Mono<Instructor> getOrCreateInstructorById(final UUID instructorId) {
        log.info("Getting instructor by id {}", instructorId);
        return userProvider.findUserById(instructorId)
                .filter(this::isInstructor)
                .map(this::resolveInstructor)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("Instructor with id '%s' not found".formatted(instructorId))));
    }

    private Instructor resolveInstructor(final UserDTO userDTO) {
        return instructorRepository.findById(userDTO.getId())
                .orElseGet(() -> {
                    final var instructor = new Instructor().setId(userDTO.getId());
                    return instructorRepository.save(instructor);
                });
    }

    private boolean isInstructor(final UserDTO userDTO) {

        if (userDTO.isInstructor()) {
            return true;
        }

        final var message = "Cannot assign instructor to course. User with id '%s' has no rights".formatted(userDTO.getId());
        throw new UserIsNotInstructorException(message);
    }
}
