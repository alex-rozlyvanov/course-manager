package com.goals.course.manager.service;

import com.goals.course.manager.configuration.AuthenticatorURL;
import com.goals.course.manager.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserProvider {
    private final WebClient webClient;
    private final AuthenticatorURL authenticatorURL;

    public Mono<UserDTO> findUserById(final UUID userId) {
        log.info("Getting user by id '{}'", userId);
        final var url = authenticatorURL.userById(userId);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(UserDTO.class);
    }
}
