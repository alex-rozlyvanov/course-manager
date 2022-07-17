package com.goals.course.manager.configuration.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@AllArgsConstructor
public class DefaultSecurityContextRepository implements ServerSecurityContextRepository {
    private final ReactiveAuthenticationManager authenticationManager;


    public Mono<Void> save(final ServerWebExchange exchange, final SecurityContext context) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        final var header = exchange
                .getRequest()
                .getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return Mono.just(new SecurityContextImpl(null));
        }

        final var authToken = header.split(" ")[1].trim();
        final var auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
        final var authenticate = authenticationManager.authenticate(auth);

        return authenticate.map(SecurityContextImpl::new);
    }
}
