package com.goals.course.manager.service;

import com.goals.course.manager.dto.UserDTO;

import java.util.Optional;
import java.util.UUID;

public interface UserProvider {
    Optional<UserDTO> findUserById(final UUID userId);
}
