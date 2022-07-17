package com.goals.course.manager.configuration;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AddTokenFilter implements ExchangeFilterFunction {

    private final ReactiveRequestContextHolder reactiveRequestContextHolder;


    public Mono<ClientResponse> filter(final ClientRequest request, final ExchangeFunction next) {

        return reactiveRequestContextHolder.getAuthToken()
                .flatMap(authToken -> {
                    final var newRequest = ClientRequest.from(request)
                            .header(HttpHeaders.AUTHORIZATION, authToken)
                            .build();
                    return next.exchange(newRequest);
                });
    }

}
