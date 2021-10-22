package com.goals.course.manager.service;

import com.goals.course.manager.dto.UserDTO;

public interface JwtTokenService {
    boolean validate(final String token);

    UserDTO getUserDTO(final String token);
}
