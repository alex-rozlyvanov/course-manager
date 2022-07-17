package com.goals.course.manager.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.goals.course.manager.configuration.AuthenticatorURL;
import com.goals.course.manager.dto.UserDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProviderTest {

    private static WireMockServer wireMockServer;
    private static String baseUrl;
    @Mock
    private AuthenticatorURL mockAuthenticatorURL;
    private UserProvider service;

    @BeforeEach
    public void before() {
        service = new UserProvider(
                WebClient.builder().build(),
                mockAuthenticatorURL
        );
    }

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        baseUrl = wireMockServer.baseUrl();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void findUserById_callUserById() {
        // GIVEN
        stubGetUserId();
        when(mockAuthenticatorURL.userById(any())).thenReturn(baseUrl + "/test/url");

        // WHEN
        service.findUserById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

        // THEN
        verify(mockAuthenticatorURL).userById(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }

    @Test
    void findUserById_checkUserByIdURL() {
        // GIVEN
        stubGetUserId();
        when(mockAuthenticatorURL.userById(any())).thenReturn(baseUrl + "/test/url");

        // WHEN
        service.findUserById(null);

        // THEN
        wireMockServer.verify(getRequestedFor(urlEqualTo("/test/url")));
    }

    @Test
    void findUserById_checkResult() {
        // GIVEN
        stubGetUserId();
        when(mockAuthenticatorURL.userById(any())).thenReturn(baseUrl + "/test/url");

        // WHEN
        final var mono = service.findUserById(null);

        // THEN
        final var userDTO = UserDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
                .build();
        StepVerifier.create(mono)
                .expectNext(userDTO)
                .verifyComplete();
    }

    private void stubGetUserId() {
        wireMockServer.stubFor(get(urlEqualTo("/test/url"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"id\":\"00000000-0000-0000-0000-000000000001\"}")));
    }

}
