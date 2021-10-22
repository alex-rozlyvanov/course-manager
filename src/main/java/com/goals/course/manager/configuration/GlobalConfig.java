package com.goals.course.manager.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;
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
    public RestTemplate restTemplate(final TokenInterceptor testInterceptor) {
        final var restTemplate = new RestTemplate();
        final var interceptors = restTemplate.getInterceptors();
        interceptors.add(testInterceptor);
        return restTemplate;
    }

    @Bean
    @RequestScope
    public TokenInterceptor testInterceptor(final HttpServletRequest request) {
        return new TokenInterceptor(request.getHeader("Authorization"));
    }

}
