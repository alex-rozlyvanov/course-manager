package com.goals.course.manager.service.implementation;

import com.goals.course.manager.configuration.AuthenticatorURL;
import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.service.UserProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@AllArgsConstructor
public class UserProviderImpl implements UserProvider {
    private final RestTemplate restTemplate;
    private final AuthenticatorURL authenticatorURL;

    @Override
    public Optional<UserDTO> findUserById(final UUID userId) {
        log.info("Getting user by id '{}'", userId);
        final var url = authenticatorURL.userById(userId);
        return ofNullable(restTemplate.getForObject(url, UserDTO.class));
    }
}
