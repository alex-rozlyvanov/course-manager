package com.goals.course.manager.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Clock;
import java.time.ZoneOffset;

@Configuration
public class GlobalConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneOffset.UTC);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(final AddTokenFilter addTokenFilter) {
        return WebClient.builder()
                .filter(addTokenFilter);
    }

    @Bean
    public WebClient webClient(final WebClient.Builder builder) {
        return builder.build();
    }
}
