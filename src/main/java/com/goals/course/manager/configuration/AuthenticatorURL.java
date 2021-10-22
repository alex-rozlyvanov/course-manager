package com.goals.course.manager.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
@Service
public class AuthenticatorURL {

    @Value("${app.microservice.authorization.url}")
    private String baseUrl;

    public String usersCurrent() {
        return baseUrl + "/api/authenticator/users/current";
    }

    public String userById(final UUID userId) {
        return baseUrl + "/api/authenticator/users/%s".formatted(userId);
    }

}
