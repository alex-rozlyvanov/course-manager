package com.goals.course.manager.service;

import com.goals.course.manager.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class SecurityService {
    public Mono<UserDTO> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(c -> (UserDTO) c.getAuthentication().getPrincipal());
    }
}
