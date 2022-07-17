package com.goals.course.manager.configuration;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ReactiveRequestContextHolder {
    public static final String AUTH_TOKEN = "auth_token";

    public Mono<String> getAuthToken() {
        return Mono.deferContextual(contextView -> Mono.just(contextView.get(AUTH_TOKEN)));
    }
}
