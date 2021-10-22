package com.goals.course.manager.service.implementation;

import com.goals.course.manager.dao.entity.Instructor;
import com.goals.course.manager.dao.enums.Roles;
import com.goals.course.manager.dao.repository.InstructorRepository;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.UserIsNotInstructorException;
import com.goals.course.manager.exception.UserNotFoundException;
import com.goals.course.manager.service.InstructorService;
import com.goals.course.manager.service.UserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class InstructorServiceImpl implements InstructorService {
    private final UserProvider userProvider;
    private final InstructorRepository instructorRepository;

    @Override
    public Instructor getOrCreateInstructorById(final UUID instructorId) {
        log.info("Getting instructor by id {}", instructorId);
        final var userDTO = userProvider.findUserById(instructorId);

        if (userDTO.isEmpty()) {
            throw new UserNotFoundException("Instructor with id '%s' not found".formatted(instructorId));
        }

        userDTO.ifPresent(dto -> assertThatUserIsInstructor(instructorId, dto));

        return instructorRepository.findById(instructorId)
                .orElseGet(() -> new Instructor().setId(instructorId));
    }

    private void assertThatUserIsInstructor(final UUID instructorId, final UserDTO userDTO) {
        final var roles = userDTO.getRoles();

        if (roles.isEmpty() ||
                roles.stream().noneMatch(r -> Roles.INSTRUCTOR.name().equals(r.getTitle()))) {
            final var message = "Cannot assign instructor to course. User with id '%s' has no rights".formatted(instructorId);
            throw new UserIsNotInstructorException(message);
        }
    }
}
