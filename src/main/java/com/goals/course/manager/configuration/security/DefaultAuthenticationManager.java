package com.goals.course.manager.configuration.security;

import com.goals.course.manager.dto.UserDTO;
import com.goals.course.manager.exception.UnauthorizedException;
import com.goals.course.manager.service.JwtTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultAuthenticationManager implements ReactiveAuthenticationManager {
    private final JwtTokenService jwtTokenService;


    public Mono<Authentication> authenticate(final Authentication authentication) throws AuthenticationException {
        final var authToken = authentication.getCredentials().toString();

        if (!jwtTokenService.validate(authToken)) {
            return Mono.error(() -> new UnauthorizedException("Token not valid"));
        }

        return Mono.just(getAuthenticationToken(authToken));
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(final String token) {
        final var userDetails = getUserDetails(token);
        return buildAuthenticationToken(userDetails);
    }

    private UserDTO getUserDetails(final String token) {
        return jwtTokenService.getUserDTO(token);
    }

    private UsernamePasswordAuthenticationToken buildAuthenticationToken(final UserDTO userDetails) {
        final var authorities = userDetails.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getTitle()))
                .toList();

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                authorities
        );
    }
}
