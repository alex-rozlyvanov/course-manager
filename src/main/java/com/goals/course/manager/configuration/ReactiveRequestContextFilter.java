package com.goals.course.manager.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContextFilter implements WebFilter {


    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final var authorizationToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.putNonNull(ReactiveRequestContextHolder.AUTH_TOKEN, authorizationToken));
    }

}
